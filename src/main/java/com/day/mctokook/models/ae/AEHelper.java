package com.day.mctokook.models.ae;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.day.mctokook.Config;
import com.day.mctokook.models.ae.entity.CPUInfo;
import com.day.mctokook.models.ae.entity.CPUInfoResponse;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;

public class AEHelper {

    private static boolean isListen = false;
    private static Thread timerThread;
    private static List<Integer> queryIds = new ArrayList<>();
    private static List<CPUInfo> cpuInfos = new ArrayList<>();

    public static boolean OrderItem(String item, int meta, int count) {
        String response = HttpUtil.get(StrUtil.format(Config.AE_ORDER_ITEM_API + "{}/{}/{}", count, meta, item));
        return Boolean.parseBoolean(response);
    }

    public static CPUInfoResponse getBusyCPUs() {
        return JSONUtil.toBean(HttpUtil.get(Config.CPU_INFO_API), CPUInfoResponse.class);
    }

    public static void handleCPUTask() {
        if (timerThread == null) {
            timerThread = new Thread(() -> {
                while (isListen) {
                    String cpu_info_response = HttpUtil.get(Config.CPU_INFO_API);
                    CPUInfoResponse cpu_info = JSONUtil.toBean(cpu_info_response, CPUInfoResponse.class);
                    if (cpu_info.infos.size() != cpuInfos.size()) {
                        cpuInfos = cpu_info.infos;
                    }

                    try {
                        Thread.sleep(TimeUnit.MILLISECONDS.toMinutes(10));
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        }
    }
}
