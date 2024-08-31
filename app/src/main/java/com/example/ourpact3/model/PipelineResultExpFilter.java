package com.example.ourpact3.model;

public class PipelineResultExpFilter extends PipelineResultBase
{
    PipelineResultExpFilter()
    {
        this.windowAction = PipelineWindowAction.PERFORM_HOME_BUTTON_AND_WARNING;
    }
    public long blockedTil;
    @Override
    public String getDialogTitle()
    {
        return "Blocked " + this.triggerApp;
    }

    @Override
    public String getDialogText()
    {
        long currentTime = System.currentTimeMillis();
        long timeDiff = currentTime -blockedTil;
        long seconds = timeDiff / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        String timeDiffString;
        if (hours > 0) {
            timeDiffString = String.format("%d hours %02d minutes", hours, minutes % 60);
        } else if (minutes > 0) {
            timeDiffString = String.format("%d minutes %02d seconds", minutes, seconds % 60);
        } else {
            timeDiffString = String.format("%d seconds", seconds);
        }
        return "This app is still blocked for " + timeDiffString;
    }
}
