#include <vulkan/vulkan.h>

#include <dlfcn.h>
#include <cstdint>
#include <iostream>
#include <string>
#include <vector>

namespace {
constexpr const char* kLoaderPath = "/system/lib64/libvulkan.so";

bool checkResult(const char* name, VkResult result) {
    std::cout << name << "_RESULT=" << static_cast<int>(result) << "\n";
    if (result != VK_SUCCESS) {
        std::cerr << "[FAIL] " << name << " returned VkResult "
                  << static_cast<int>(result) << "\n";
        return false;
    }
    return true;
}

void printQueueFlags(VkQueueFlags flags) {
    std::cout << "  GRAPHICS=" << ((flags & VK_QUEUE_GRAPHICS_BIT) ? "YES" : "NO")
              << " COMPUTE=" << ((flags & VK_QUEUE_COMPUTE_BIT) ? "YES" : "NO")
              << " TRANSFER=" << ((flags & VK_QUEUE_TRANSFER_BIT) ? "YES" : "NO")
              << " SPARSE_BINDING=" << ((flags & VK_QUEUE_SPARSE_BINDING_BIT) ? "YES" : "NO")
              << "\n";
}
} // namespace

int main() {
    std::cout << "===== CASTAWAY VULKAN DEVICE PROBE =====\n";
    std::cout << "LOADER_PATH=" << kLoaderPath << "\n";

    void* library = dlopen(kLoaderPath, RTLD_NOW | RTLD_LOCAL);
    if (!library) {
        const char* error = dlerror();
        std::cerr << "[FAIL] dlopen: " << (error ? error : "unknown error") << "\n";
        return 10;
    }
    std::cout << "DLOPEN=PASS\n";

    auto getProc = reinterpret_cast<PFN_vkGetInstanceProcAddr>(
        dlsym(library, "vkGetInstanceProcAddr"));
    if (!getProc) {
        const char* error = dlerror();
        std::cerr << "[FAIL] dlsym(vkGetInstanceProcAddr): "
                  << (error ? error : "symbol not found") << "\n";
        dlclose(library);
        return 11;
    }
    std::cout << "GIPA=PASS\n";

    auto enumerateVersion = reinterpret_cast<PFN_vkEnumerateInstanceVersion>(
        getProc(VK_NULL_HANDLE, "vkEnumerateInstanceVersion"));
    uint32_t loaderVersion = VK_API_VERSION_1_0;
    if (enumerateVersion) {
        if (!checkResult("vkEnumerateInstanceVersion",
                         enumerateVersion(&loaderVersion))) {
            dlclose(library);
            return 12;
        }
    } else {
        std::cout << "vkEnumerateInstanceVersion=UNAVAILABLE_ASSUME_1_0\n";
    }

    const uint32_t requestedVersion =
        loaderVersion < VK_API_VERSION_1_3 ? loaderVersion : VK_API_VERSION_1_3;
    std::cout << "LOADER_API_VERSION="
              << VK_VERSION_MAJOR(loaderVersion) << "."
              << VK_VERSION_MINOR(loaderVersion) << "."
              << VK_VERSION_PATCH(loaderVersion) << "\n";
    std::cout << "REQUESTED_API_VERSION="
              << VK_VERSION_MAJOR(requestedVersion) << "."
              << VK_VERSION_MINOR(requestedVersion) << "."
              << VK_VERSION_PATCH(requestedVersion) << "\n";

    auto createInstance = reinterpret_cast<PFN_vkCreateInstance>(
        getProc(VK_NULL_HANDLE, "vkCreateInstance"));
    if (!createInstance) {
        std::cerr << "[FAIL] vkCreateInstance function pointer is null\n";
        dlclose(library);
        return 13;
    }

    VkApplicationInfo app{};
    app.sType = VK_STRUCTURE_TYPE_APPLICATION_INFO;
    app.pApplicationName = "CASTAWAY DEPTHS Vulkan Device Probe";
    app.applicationVersion = VK_MAKE_API_VERSION(0, 1, 0, 0);
    app.pEngineName = "CASTAWAY DEPTHS";
    app.engineVersion = VK_MAKE_API_VERSION(0, 1, 0, 0);
    app.apiVersion = requestedVersion;

    VkInstanceCreateInfo instanceInfo{};
    instanceInfo.sType = VK_STRUCTURE_TYPE_INSTANCE_CREATE_INFO;
    instanceInfo.pApplicationInfo = &app;

    VkInstance instance = VK_NULL_HANDLE;
    if (!checkResult("vkCreateInstance",
                     createInstance(&instanceInfo, nullptr, &instance)) ||
        instance == VK_NULL_HANDLE) {
        std::cerr << "[FAIL] Vulkan instance was not created\n";
        dlclose(library);
        return 14;
    }
    std::cout << "INSTANCE_CREATE=PASS\n";

    auto destroyInstance = reinterpret_cast<PFN_vkDestroyInstance>(
        getProc(instance, "vkDestroyInstance"));
    auto enumeratePhysicalDevices = reinterpret_cast<PFN_vkEnumeratePhysicalDevices>(
        getProc(instance, "vkEnumeratePhysicalDevices"));
    auto getQueueFamilyProperties =
        reinterpret_cast<PFN_vkGetPhysicalDeviceQueueFamilyProperties>(
            getProc(instance, "vkGetPhysicalDeviceQueueFamilyProperties"));
    auto getPhysicalDeviceProperties =
        reinterpret_cast<PFN_vkGetPhysicalDeviceProperties>(
            getProc(instance, "vkGetPhysicalDeviceProperties"));
    auto createDevice = reinterpret_cast<PFN_vkCreateDevice>(
        getProc(instance, "vkCreateDevice"));

    if (!destroyInstance || !enumeratePhysicalDevices ||
        !getQueueFamilyProperties || !getPhysicalDeviceProperties || !createDevice) {
        std::cerr << "[FAIL] Required instance-level Vulkan function pointer missing\n";
        if (destroyInstance) destroyInstance(instance, nullptr);
        dlclose(library);
        return 15;
    }

    uint32_t physicalCount = 0;
    if (!checkResult("vkEnumeratePhysicalDevices(count)",
                     enumeratePhysicalDevices(instance, &physicalCount, nullptr))) {
        destroyInstance(instance, nullptr);
        dlclose(library);
        return 16;
    }
    std::cout << "PHYSICAL_DEVICE_COUNT=" << physicalCount << "\n";
    if (physicalCount == 0) {
        std::cerr << "[FAIL] No physical devices enumerated\n";
        destroyInstance(instance, nullptr);
        dlclose(library);
        return 17;
    }

    std::vector<VkPhysicalDevice> physicalDevices(physicalCount, VK_NULL_HANDLE);
    if (!checkResult("vkEnumeratePhysicalDevices(list)",
                     enumeratePhysicalDevices(instance, &physicalCount,
                                               physicalDevices.data()))) {
        destroyInstance(instance, nullptr);
        dlclose(library);
        return 18;
    }
    physicalDevices.resize(physicalCount);

    bool deviceCreated = false;
    int exitCode = 0;
    for (uint32_t deviceIndex = 0; deviceIndex < physicalCount; ++deviceIndex) {
        const VkPhysicalDevice physical = physicalDevices[deviceIndex];
        VkPhysicalDeviceProperties properties{};
        getPhysicalDeviceProperties(physical, &properties);

        std::cout << "\nDEVICE_INDEX=" << deviceIndex << "\n";
        std::cout << "DEVICE_NAME=" << properties.deviceName << "\n";
        std::cout << "DEVICE_API_VERSION="
                  << VK_VERSION_MAJOR(properties.apiVersion) << "."
                  << VK_VERSION_MINOR(properties.apiVersion) << "."
                  << VK_VERSION_PATCH(properties.apiVersion) << "\n";
        std::cout << "VENDOR_ID=0x" << std::hex << properties.vendorID
                  << " DEVICE_ID=0x" << properties.deviceID << std::dec << "\n";

        uint32_t familyCount = 0;
        getQueueFamilyProperties(physical, &familyCount, nullptr);
        std::cout << "QUEUE_FAMILY_COUNT=" << familyCount << "\n";
        if (familyCount == 0) {
            std::cout << "QUEUE_FAMILY_STATUS=NONE\n";
            continue;
        }

        std::vector<VkQueueFamilyProperties> families(familyCount);
        getQueueFamilyProperties(physical, &familyCount, families.data());
        families.resize(familyCount);

        uint32_t graphicsFamily = UINT32_MAX;
        for (uint32_t familyIndex = 0; familyIndex < familyCount; ++familyIndex) {
            const auto& family = families[familyIndex];
            std::cout << "QUEUE_FAMILY_" << familyIndex
                      << "_QUEUE_COUNT=" << family.queueCount << "\n";
            std::cout << "QUEUE_FAMILY_" << familyIndex << "_FLAGS=";
            printQueueFlags(family.queueFlags);
            if (graphicsFamily == UINT32_MAX && family.queueCount > 0 &&
                (family.queueFlags & VK_QUEUE_GRAPHICS_BIT)) {
                graphicsFamily = familyIndex;
            }
        }

        if (graphicsFamily == UINT32_MAX) {
            std::cout << "GRAPHICS_QUEUE_FAMILY=NOT_FOUND\n";
            continue;
        }
        std::cout << "GRAPHICS_QUEUE_FAMILY=" << graphicsFamily << "\n";

        const float priority = 1.0f;
        VkDeviceQueueCreateInfo queueInfo{};
        queueInfo.sType = VK_STRUCTURE_TYPE_DEVICE_QUEUE_CREATE_INFO;
        queueInfo.queueFamilyIndex = graphicsFamily;
        queueInfo.queueCount = 1;
        queueInfo.pQueuePriorities = &priority;

        VkDeviceCreateInfo deviceInfo{};
        deviceInfo.sType = VK_STRUCTURE_TYPE_DEVICE_CREATE_INFO;
        deviceInfo.queueCreateInfoCount = 1;
        deviceInfo.pQueueCreateInfos = &queueInfo;

        VkDevice device = VK_NULL_HANDLE;
        const VkResult createResult = createDevice(physical, &deviceInfo, nullptr, &device);
        if (!checkResult("vkCreateDevice", createResult) || device == VK_NULL_HANDLE) {
            std::cerr << "[FAIL] Logical device creation failed for device "
                      << deviceIndex << "\n";
            exitCode = 20;
            continue;
        }
        std::cout << "LOGICAL_DEVICE_CREATE=PASS\n";

        auto getDeviceProc = reinterpret_cast<PFN_vkGetDeviceProcAddr>(
            getProc(instance, "vkGetDeviceProcAddr"));
        if (!getDeviceProc) {
            std::cerr << "[FAIL] vkGetDeviceProcAddr pointer is null\n";
            auto destroyDevice = reinterpret_cast<PFN_vkDestroyDevice>(
                getProc(instance, "vkDestroyDevice"));
            if (destroyDevice) destroyDevice(device, nullptr);
            exitCode = 21;
            continue;
        }
        auto getDeviceQueue = reinterpret_cast<PFN_vkGetDeviceQueue>(
            getDeviceProc(device, "vkGetDeviceQueue"));
        auto destroyDevice = reinterpret_cast<PFN_vkDestroyDevice>(
            getDeviceProc(device, "vkDestroyDevice"));
        if (!getDeviceQueue || !destroyDevice) {
            std::cerr << "[FAIL] Required device-level function pointer missing\n";
            if (destroyDevice) destroyDevice(device, nullptr);
            exitCode = 22;
            continue;
        }

        VkQueue queue = VK_NULL_HANDLE;
        getDeviceQueue(device, graphicsFamily, 0, &queue);
        if (queue == VK_NULL_HANDLE) {
            std::cerr << "[FAIL] vkGetDeviceQueue returned a null queue\n";
            destroyDevice(device, nullptr);
            exitCode = 23;
            continue;
        }
        std::cout << "VK_GET_DEVICE_QUEUE=PASS\n";
        std::cout << "QUEUE_HANDLE_NON_NULL=YES\n";
        destroyDevice(device, nullptr);
        std::cout << "VK_DESTROY_DEVICE=PASS\n";
        deviceCreated = true;
        break;
    }

    destroyInstance(instance, nullptr);
    std::cout << "VK_DESTROY_INSTANCE=PASS\n";
    dlclose(library);
    std::cout << "DLCLOSE=PASS\n";

    if (deviceCreated && exitCode == 0) {
        std::cout << "VULKAN_QUEUE_DEVICE_TEST=PASS\n";
        return 0;
    }
    std::cout << "VULKAN_QUEUE_DEVICE_TEST=FAIL\n";
    return exitCode == 0 ? 24 : exitCode;
}
