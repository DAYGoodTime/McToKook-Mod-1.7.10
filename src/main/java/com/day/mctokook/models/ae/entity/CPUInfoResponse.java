package com.day.mctokook.models.ae.entity;

import java.util.List;

public class CPUInfoResponse {

    public int idle_cpus;
    public int total_cpus;
    public List<CPUInfo> infos;

    public CPUInfoResponse(int idle_cpus, int total_cpus, List<CPUInfo> infos) {
        this.idle_cpus = idle_cpus;
        this.total_cpus = total_cpus;
        this.infos = infos;
    }
}
