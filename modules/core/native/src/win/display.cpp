#include "grapl-win.h"

#include <Shellscalingapi.h>
#include <SetupApi.h>
#include <cfgmgr32.h>
#include <vector>

struct MonitorEnum{
    std::vector<jlong> handles;

    static BOOL CALLBACK MonitorEnumProc(HMONITOR hMon, HDC hdc, LPRECT lprcMonitor, LPARAM pData){
        MonitorEnum* pThis = reinterpret_cast<MonitorEnum*>(pData);
        pThis->handles.push_back((jlong)hMon);
        return TRUE;
    }

    MonitorEnum() {
        EnumDisplayMonitors(0, 0, MonitorEnumProc, (LPARAM)this);
    }
};

static int getEDID(HMONITOR monitor, BYTE* dataEDID, int dataLength){
    MONITORINFOEXW info;
    info.cbSize = sizeof(info);
    GetMonitorInfoW((HMONITOR)monitor, &info);

    WCHAR* targetMonitorDevicePath = NULL;

    /* ========================================== *\
                    Find display id
    /* ========================================== */
    UINT32 pathCount, modeCount;
    GetDisplayConfigBufferSizes(QDC_ONLY_ACTIVE_PATHS, &pathCount, &modeCount);

    std::vector<DISPLAYCONFIG_PATH_INFO> paths(pathCount);
    std::vector<DISPLAYCONFIG_MODE_INFO> modes(modeCount);
    QueryDisplayConfig(QDC_ONLY_ACTIVE_PATHS, &pathCount, paths.data(), &modeCount, modes.data(), nullptr);

    for (int i = 0; i < paths.size(); i++) {
        DISPLAYCONFIG_SOURCE_DEVICE_NAME sourceName;
        sourceName.header.type = DISPLAYCONFIG_DEVICE_INFO_GET_SOURCE_NAME;
        sourceName.header.size = sizeof(sourceName);
        sourceName.header.adapterId = paths[i].sourceInfo.adapterId;
        sourceName.header.id = paths[i].sourceInfo.id;
        DisplayConfigGetDeviceInfo(&sourceName.header);

        DISPLAYCONFIG_TARGET_DEVICE_NAME targetName;
        targetName.header.type = DISPLAYCONFIG_DEVICE_INFO_GET_TARGET_NAME;
        targetName.header.size = sizeof(targetName);
        targetName.header.adapterId = paths[i].sourceInfo.adapterId;
        targetName.header.id = paths[i].targetInfo.id;
        DisplayConfigGetDeviceInfo(&targetName.header);

        if (wcscmp(info.szDevice, sourceName.viewGdiDeviceName) == 0) {
            targetMonitorDevicePath = targetName.monitorDevicePath;
            break;
        }
    }

    /* ========================================== *\
              Query found id with registry
    /* ========================================== */
    const GUID GUID_DEVINTERFACE_MONITOR = { 0xe6f07b5f, 0xee97, 0x4a90, 0xb0, 0x76, 0x33, 0xf5, 0x7b, 0xf4, 0xea, 0xa7 };
    const HDEVINFO hDevInfo = SetupDiGetClassDevs(&GUID_DEVINTERFACE_MONITOR, NULL, NULL, DIGCF_DEVICEINTERFACE);
    wchar_t devPathBuffer[sizeof(SP_DEVICE_INTERFACE_DETAIL_DATA_W) + (128 * sizeof(wchar_t))];

    SP_DEVICE_INTERFACE_DATA devInfo;
    devInfo.cbSize = sizeof(devInfo);

    DWORD monitorIndex = 0;
    while(SetupDiEnumDeviceInterfaces(hDevInfo, NULL, &GUID_DEVINTERFACE_MONITOR, monitorIndex, &devInfo)){
        monitorIndex++;

        SP_DEVICE_INTERFACE_DETAIL_DATA_W* devPathData = (SP_DEVICE_INTERFACE_DETAIL_DATA_W*)devPathBuffer;
        devPathData->cbSize = sizeof(SP_DEVICE_INTERFACE_DETAIL_DATA_W);

        SP_DEVINFO_DATA devInfoData = {};
        devInfoData.cbSize = sizeof(devInfoData);

        SetupDiGetDeviceInterfaceDetailW(hDevInfo, &devInfo, devPathData, sizeof(devPathBuffer), NULL, &devInfoData);

        WCHAR* deviceId = devPathData->DevicePath;
        if(_wcsicmp(targetMonitorDevicePath, deviceId) != 0)
            continue;

        wchar_t instanceId[MAX_DEVICE_ID_LEN];
        SetupDiGetDeviceInstanceIdW(hDevInfo, &devInfoData, instanceId, MAX_PATH, NULL);

        HKEY hEDIDRegKey = SetupDiOpenDevRegKey(hDevInfo, &devInfoData, DICS_FLAG_GLOBAL, 0, DIREG_DEV, KEY_READ);
        if(!hEDIDRegKey || (hEDIDRegKey == INVALID_HANDLE_VALUE))
            continue;

        DWORD sizeOfDataEDID = dataLength;
        if(RegQueryValueExW(hEDIDRegKey, L"EDID", NULL, NULL, dataEDID, &sizeOfDataEDID) == ERROR_SUCCESS){
            RegCloseKey(hEDIDRegKey);
            return sizeOfDataEDID;
        }
        RegCloseKey(hEDIDRegKey);
    }
    return false;
}

jni_win_display(jlong, nGetPrimaryMonitor)(JNIEnv* env, jobject) {
    const POINT zero = { 0, 0 };
    return (jlong) MonitorFromPoint(zero, MONITOR_DEFAULTTOPRIMARY);
}

jni_win_display(jlongArray, nGetAllMonitors)(JNIEnv* env, jobject) {
    MonitorEnum monitorEnum;
    return createLongArray(env, monitorEnum.handles);
}

jni_win_display(jintArray, nGetSize)(JNIEnv* env, jobject, jlong monitor) {
    MONITORINFOEX info = { };
    info.cbSize = sizeof(info);

    GetMonitorInfo((HMONITOR)monitor, &info);

    return createIntArray(env, {
        info.rcMonitor.right - info.rcMonitor.left,
        info.rcMonitor.bottom - info.rcMonitor.top
    });
}

jni_win_display(jintArray, nGetPosition)(JNIEnv* env, jobject, jlong monitor) {
    MONITORINFOEX info = { };
    info.cbSize = sizeof(info);

    GetMonitorInfo((HMONITOR)monitor, &info);

    return createIntArray(env, {
        info.rcMonitor.left,
        info.rcMonitor.top
    });
}

jni_win_display(jdouble, nGetDpi)(JNIEnv* env, jobject, jlong monitor) {
    MONITORINFOEXW info = { };
    info.cbSize = sizeof(info);

    GetMonitorInfoW((HMONITOR)monitor, &info);

    DEVMODE devmode = {};
    devmode.dmSize = sizeof(devmode);
    EnumDisplaySettings(info.szDevice, ENUM_CURRENT_SETTINGS, &devmode);
    float pureDpi = (info.rcMonitor.right - info.rcMonitor.left) / (float)devmode.dmPelsWidth;

    return (jdouble)(GetDpiForSystem() / 96.0 / pureDpi);
}

jni_win_display(jint, nGetFrequency)(JNIEnv* env, jobject, jlong monitor) {
    MONITORINFOEXW info;
    info.cbSize = sizeof(info);
    GetMonitorInfoW((HMONITOR)monitor, &info);

    DEVMODE devmode = {};
    devmode.dmSize = sizeof(devmode);
    EnumDisplaySettingsW(info.szDevice, ENUM_CURRENT_SETTINGS, &devmode);
    return devmode.dmDisplayFrequency;
}

jni_win_display(jstring, nGetName)(JNIEnv* env, jobject, jlong monitor) {
    MONITORINFOEXW info;
    info.cbSize = sizeof(info);
    GetMonitorInfoW((HMONITOR)monitor, &info);

    UINT32 pathCount, modeCount;
    GetDisplayConfigBufferSizes(QDC_ONLY_ACTIVE_PATHS, &pathCount, &modeCount);

    std::vector<DISPLAYCONFIG_PATH_INFO> paths(pathCount);
    std::vector<DISPLAYCONFIG_MODE_INFO> modes(modeCount);
    QueryDisplayConfig(QDC_ONLY_ACTIVE_PATHS, &pathCount, paths.data(), &modeCount, modes.data(), nullptr);

    for (int i = 0; i < paths.size(); i++) {
        DISPLAYCONFIG_SOURCE_DEVICE_NAME sourceName;
        sourceName.header.type = DISPLAYCONFIG_DEVICE_INFO_GET_SOURCE_NAME;
        sourceName.header.size = sizeof(sourceName);
        sourceName.header.adapterId = paths[i].sourceInfo.adapterId;
        sourceName.header.id = paths[i].sourceInfo.id;
        DisplayConfigGetDeviceInfo(&sourceName.header);

        DISPLAYCONFIG_TARGET_DEVICE_NAME targetName;
        targetName.header.type = DISPLAYCONFIG_DEVICE_INFO_GET_TARGET_NAME;
        targetName.header.size = sizeof(targetName);
        targetName.header.adapterId = paths[i].sourceInfo.adapterId;
        targetName.header.id = paths[i].targetInfo.id;
        DisplayConfigGetDeviceInfo(&targetName.header);

        if (wcscmp(info.szDevice, sourceName.viewGdiDeviceName) == 0) {
            WCHAR* name = targetName.monitorFriendlyDeviceName;
            return env->NewString((jchar*)name, (jsize)wcslen(name));
        }
    }
    return env->NewStringUTF("Unknown");
}

jni_win_display(jstring, nGetSystemName)(JNIEnv* env, jobject, jlong monitor) {
    MONITORINFOEXW info;
    info.cbSize = sizeof(info);
    GetMonitorInfoW((HMONITOR)monitor, &info);

    return env->NewString((jchar*)info.szDevice, (jsize)wcslen(info.szDevice));
}

jni_win_display(jintArray, nGetPhysicalSize)(JNIEnv* env, jobject, jlong monitor) {
    BYTE edid[1024];
    if(getEDID((HMONITOR)monitor, edid, sizeof(edid))){
        return createIntArray(env, {
            ((edid[68] & 0xF0) << 4) + edid[66],
            ((edid[68] & 0x0F) << 8) + edid[67]
        });
    }
    return createIntArray(env, { 0, 0 });
}

jni_win_display(jintArray, nGetDisplayModes)(JNIEnv* env, jobject, jlong monitor) {
    MONITORINFOEXW info;
    info.cbSize = sizeof(info);
    GetMonitorInfoW((HMONITOR)monitor, &info);

    std::vector<jint> result;
    DEVMODE dm;
    DWORD iModeNum = 0;
    while (EnumDisplaySettings(info.szDevice, iModeNum++, &dm)){
        result.push_back(dm.dmPelsWidth);
        result.push_back(dm.dmPelsHeight);
        result.push_back(dm.dmBitsPerPel);
        result.push_back(dm.dmDisplayFrequency);
    }
    return createIntArray(env, result);
}

jni_win_display(jintArray, nGetCurrentDisplayMode)(JNIEnv* env, jobject, jlong monitor) {
    MONITORINFOEXW info;
    info.cbSize = sizeof(info);
    GetMonitorInfoW((HMONITOR)monitor, &info);

    DEVMODE dm;
    EnumDisplaySettings(info.szDevice, ENUM_CURRENT_SETTINGS, &dm);
    return createIntArray(env, {
        (jint) dm.dmPelsWidth,
        (jint) dm.dmPelsHeight,
        (jint) dm.dmBitsPerPel,
        (jint) dm.dmDisplayFrequency
    });
}

jni_win_display(jbyteArray, nGetEDID)(JNIEnv* env, jobject, jlong monitor) {
    BYTE edid[4096];
    int size = getEDID((HMONITOR)monitor, edid, sizeof(edid));
    if(size > 0)
        return createByteArray(env, size, (jbyte*)edid);
    return createByteArray(env, {});
}