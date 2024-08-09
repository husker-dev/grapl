#include <windows.h>
#include <windowsx.h>
#include <directmanipulation.h>
#include <wrl.h>
#include <cmath>
#include <map>

#define IDT_TOUCHPAD 1381

#define TM_SCROLL_BEGIN 0x1250
#define TM_SCROLL 0x1251
#define TM_SCROLL_END 0x1252

#define TM_SCALE_BEGIN 0x1253
#define TM_SCALE 0x1254
#define TM_SCALE_END 0x1255

static WPARAM MAKE_DOUBLE_W(float value) {
	uint64_t u;
	memcpy(&u, &value, sizeof(value));
	return (WPARAM)u;
}

static WPARAM MAKE_DOUBLE_L(float value) {
	uint64_t u;
	memcpy(&u, &value, sizeof(value));
	return (WPARAM)(u >> 32);
}

static float FLOAT_FROM_PARAMS(WPARAM wParam, LPARAM lParam) {
	uint64_t ui = wParam | (uint64_t)lParam << 32;
	float res;
	memcpy(&res, &ui, sizeof(ui));
	return res;
}


static std::map<HWND, void**> boundTrackpads;

class TouchpadManager {
public:
	class Handler;

	HWND hwnd;
	WNDPROC prevProc;

	Microsoft::WRL::ComPtr<IDirectManipulationManager> manager;
	Microsoft::WRL::ComPtr<IDirectManipulationUpdateManager> updateManager;
	Microsoft::WRL::ComPtr<IDirectManipulationViewport> viewport;
	Microsoft::WRL::ComPtr<Handler> handler;
	DWORD cookie = 0;

	static LRESULT CALLBACK CustomWinProc(HWND hwnd, UINT msg, WPARAM wParam, LPARAM lParam) {
		TouchpadManager* touchpadManager = (TouchpadManager*)boundTrackpads[hwnd];
		switch (msg) {
		case DM_POINTERHITTEST: {
			touchpadManager->handlePointerHitTest(wParam);
			break;
		}
		case WM_SIZE: {
			touchpadManager->updateViewportSize();
			break;
		}
		}
		return CallWindowProcA(touchpadManager->prevProc, hwnd, msg, wParam, lParam);
	}


	TouchpadManager(HWND hwnd) {
		this->hwnd = hwnd;
		boundTrackpads[hwnd] = (void**)this;

		prevProc = (WNDPROC)GetWindowLongPtr(hwnd, GWLP_WNDPROC);
		SetWindowLongPtr(hwnd, GWLP_WNDPROC, (LONG_PTR)CustomWinProc);

		CoInitialize(NULL);
		CoCreateInstance(CLSID_DirectManipulationManager, nullptr, CLSCTX_INPROC_SERVER, IID_PPV_ARGS(&manager));

		manager->GetUpdateManager(IID_PPV_ARGS(&updateManager));
		manager->CreateViewport(nullptr, hwnd, IID_PPV_ARGS(&viewport));

		viewport->ActivateConfiguration(
			DIRECTMANIPULATION_CONFIGURATION_INTERACTION |
			DIRECTMANIPULATION_CONFIGURATION_TRANSLATION_X |
			DIRECTMANIPULATION_CONFIGURATION_TRANSLATION_Y |
			DIRECTMANIPULATION_CONFIGURATION_TRANSLATION_INERTIA |
			DIRECTMANIPULATION_CONFIGURATION_SCALING |
			DIRECTMANIPULATION_CONFIGURATION_SCALING_INERTIA
		);

		viewport->SetViewportOptions(DIRECTMANIPULATION_VIEWPORT_OPTIONS_MANUALUPDATE);

		handler = Microsoft::WRL::Make<Handler>();
		handler->setParent(this);

		viewport->AddEventHandler(hwnd, handler.Get(), &cookie);

		RECT rect = { 0, 0, 1000, 1000 };
		viewport->SetViewportRect(&rect);

		updateViewportSize();

		manager->Activate(hwnd);
		viewport->Enable();
		updateManager->Update(nullptr);
	}

	~TouchpadManager() {
		boundTrackpads.erase(hwnd);

		handler->setParent(nullptr);
		handler = nullptr;

		viewport->Stop();
		viewport->RemoveEventHandler(cookie);
		viewport->Abandon();
		viewport = nullptr;

		updateManager = nullptr;

		manager->Deactivate(hwnd);
		manager = nullptr;
	}

	void handlePointerHitTest(WPARAM wParam) {
		const UINT32 id = UINT32(GET_POINTERID_WPARAM(wParam));
		POINTER_INPUT_TYPE type;
		if (GetPointerType(id, &type) && type == PT_TOUCHPAD)
			viewport->SetContact(id);
	}

	void updateViewportSize() {
		RECT rect = {};
		GetWindowRect(hwnd, &rect);

		handler->setViewportSize(rect);

		viewport->Stop();
		viewport->SetViewportRect(&rect);
	}


	class Handler : public
		Microsoft::WRL::RuntimeClass<
		Microsoft::WRL::RuntimeClassFlags<
		Microsoft::WRL::RuntimeClassType::ClassicCom>,
		Microsoft::WRL::Implements<
		Microsoft::WRL::RuntimeClassFlags<
		Microsoft::WRL::RuntimeClassType::ClassicCom>,
		Microsoft::WRL::FtmBase,
		IDirectManipulationViewportEventHandler,
		IDirectManipulationInteractionEventHandler>>
	{
	public:
		void setParent(TouchpadManager* newParent) {
			parent = newParent;
		}

		void setViewportSize(RECT rect) {
			this->width = rect.right - rect.left;
			this->height = rect.bottom - rect.top;
		}

	private:
		~Handler() {}

		enum class State {
			None,
			Scroll,
			Scale,
		};

		TouchpadManager* parent = nullptr;

		int width = 0;
		int height = 0;

		bool scrolling = false;
		bool scaling = false;


		static VOID CALLBACK TrackpadTimerProc(HWND hwnd, UINT uMsg, UINT idEvent, DWORD dwTime) {
			if (idEvent != IDT_TOUCHPAD)
				return;
			TouchpadManager* trackpadManager = (TouchpadManager*)boundTrackpads[hwnd];
			trackpadManager->updateManager->Update(nullptr);
		}

		void resetState() {
			if (scrolling) {
				scrolling = false;
				SendMessage(parent->hwnd, TM_SCROLL_END, 0, 0);
			}
			if (scaling) {
				scaling = false;
				SendMessage(parent->hwnd, TM_SCALE_END, 0, 0);
			}
		}


		HRESULT STDMETHODCALLTYPE OnViewportStatusChanged(_In_ IDirectManipulationViewport* viewport, _In_ DIRECTMANIPULATION_STATUS current, _In_ DIRECTMANIPULATION_STATUS previous) override {
			if (current == previous || current != DIRECTMANIPULATION_READY)
				return S_OK;

			resetState();
			viewport->ZoomToRect(0, 0, width, height, FALSE);
			return S_OK;
		}

		HRESULT STDMETHODCALLTYPE OnViewportUpdated(_In_ IDirectManipulationViewport* viewport) override {
			return S_OK;
		}

		HRESULT STDMETHODCALLTYPE OnContentUpdated(_In_ IDirectManipulationViewport* viewport, _In_ IDirectManipulationContent* content) override {
			float matrix[6];
			content->GetContentTransform(matrix, 6);

			if (matrix[0] != 1) {
				if (!scaling) {
					scaling = true;
					SendMessage(parent->hwnd, TM_SCALE_BEGIN, 0, 0);
				}
				SendMessage(parent->hwnd, TM_SCALE, MAKE_DOUBLE_W(matrix[0]), MAKE_DOUBLE_L(matrix[0]));
			}
			else if (matrix[4] != 0 || matrix[5] != 0) {
				if (!scrolling) {
					scrolling = true;
					SendMessage(parent->hwnd, TM_SCROLL_BEGIN, 0, 0);
				}
				SendMessage(parent->hwnd, TM_SCROLL, matrix[4], matrix[5]);
			}
			return S_OK;
		}

		HRESULT STDMETHODCALLTYPE OnInteraction(_In_ IDirectManipulationViewport2* viewport, _In_ DIRECTMANIPULATION_INTERACTION_TYPE interaction) override {
			if (interaction == DIRECTMANIPULATION_INTERACTION_BEGIN)
				SetTimer(parent->hwnd, IDT_TOUCHPAD, 1000 / 120, (TIMERPROC)TrackpadTimerProc);
			if (interaction == DIRECTMANIPULATION_INTERACTION_END) {
				KillTimer(parent->hwnd, IDT_TOUCHPAD);
				resetState();
			}
			return S_OK;
		}
	};
};


