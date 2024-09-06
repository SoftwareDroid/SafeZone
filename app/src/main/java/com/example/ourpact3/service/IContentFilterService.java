package com.example.ourpact3.service;

public interface IContentFilterService
{
    public enum Mode
    {
        NORMAL_MODE,
        APP_KILL_MODE_1,
    }
    void setMode(Mode m);
}
