package com.example.ourpact3.service;

import com.example.ourpact3.pipeline.PipelineResultBase;

public interface IFilterResultCallback
{
//    void onPipelineResult(PipelineResultBase result);
    void onPipelineResultBackground(PipelineResultBase result);
}
