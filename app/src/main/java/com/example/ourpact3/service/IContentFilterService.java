package com.example.ourpact3.service;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.NotNull;

public interface IContentFilterService
{
    public enum Mode
    {
        NORMAL_MODE,
        APP_KILL_MODE_1,
    }
    void activateAppKillMode(@NotNull String packageId);
    void finishAppKilling();
}
