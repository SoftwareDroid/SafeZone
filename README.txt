# For devs
Backlog exestiert nur aus Papier in 2 boxen 

# grant permission
adb shell settings put secure enabled_accessibility_services com.example.ourpact3/com.example.ourpact3.ContentFilterService

use: app_dev.sh # needs icon under ubuntu for start menu

Create to and from json method with AI

CHange app verion: build.gradle.kts

Wenn emulator 1x lief AndroidStudio neustarten macht sonst ziemlich Probele

KIll foreground apps not possible
KILL background not possible
Control media not possible

import android.Manifest; // needed to check permissions

# For Users:
Use as Search Engine in e.g. Firefox: safe.duckduckgo.com/?q=%s
most others are blocked by the safe search rule


USEs UI anomation to kill an app only way i found without having System or root permission to stop media (adult content) in an e.g browser

TODO
FIX Search
In Recent searches tiggert nicht die Abort Pipeplie zu erst
FIXEN PocketCasts muss bedienbar bleiben auch bei 100 scoring


### Documentation
words in Topics are scored for their nearest topic (Deep).
// Pick the topic with which is the directest e.g All contains porn and clothing. We want to count clothing with its own score


SmartFilter; Trigger + Action
Smart Bundling for Lang x for App y makes senses (sharing)

How to prevent uninstall
# Give first accessabilty permisson then device admin and go out of learn mode

# Changelog
0.* Topic Manager, Regex Support,...
1.0 App Killing with UI Automation
1.1 Learn Mode
1.1.1 Hamburger Button in Learn Mode
1.2.0 Thump Down => Block Sign + Symbols for Home,
      Use Idendifier for Word Filter
1.2.1 fix error reacalcualte expresion

Add Default/all other apps handling killing works
term Sex to regex because of unisex clothing
term slavery => remove history podcast
oral in doctoral
arsch in nachtbarschaft
eier => kommt beim kochen vor



==
public class PipelineResultView extends LinearLayout {
    public PipelineResultView(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflate(context, R.layout.pipeline_result_settings_view, this);
        init();
    }

    private void init() {
        // Hier kannst du die XML-Knoten finden und bearbeiten
        TextView textView = (TextView) findViewById(R.id.text_view);
        textView.setText("Neuer Text");

        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Code hier
            }
        });
    }
}

==
<com.example.ourpact3.ui.misc.PipelineResultView
    android:id="@+id/pipline_result_view"
    android:layout_width="match_parent"
    android:layout_height="wrap_content" />
    ==
    PipelineResultView pipelineResultView = (PipelineResultView) findViewById(R.id.pipline_result_view);