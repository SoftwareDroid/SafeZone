# For devs
Backlog exestiert nur aus Papier in 2 boxen 
Mancmal muss man die App neu installieren insbesonder für Berechtigungen Reset. Draw Overlay Permission wird z.b benötigt
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

Neue Actitäten einfach mit Rechtsklick neu erzeugen. Damit lässt sich viel Boilerplate Quellcode erstelln

##########################################################################################################################
# For Users:
Use as Search Engine in e.g. Firefox: safe.duckduckgo.com/?q=%s
most others are blocked by the safe search rule


USEs UI anomation to kill an app only way i found without having System or root permission to stop media (adult content) in an e.g browser

TODO

FAQ:
Safe Search block is blocking my search:
Use the correct search url like: safe.duckduckgo.com/?q=%s

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
1.4.0 fix AI duckduckgo and big text nodes problem => but now suffix search problem
Ignore Write fields as search aborts
1.5.0 clean up home fragment and added lock status indicator to it
1.7.0 Now unlock/lock via settings fragment
1.7.1 use correct lock timestamp in home fragment
1.7.2 prevent turning off access service plus device option do not use KILL_BEFORE_WINDOW anymore as this is to slow and allows for errors
1.8.0 seperate window and button action, introduce boolean settings enable logging (this works already and block windows)
1.9.0 first logging
1.9.1 update termlist rm clothing block image search duckduckgo not working + OnPause Event (HomeBUtton) for setttings framgment to save settings
1,9.2 Need draw overlay permission
1.9.3 rm nude in Reisnudeln
1.9.4 latex can in Naturlatex (Store) and try catch two possible exceptions in overlay window
1.9.5 remove mostly body parts words
1.10.0 explain text to bg and add \\b to popo term
1.10.1 add regex for duckduckgo ai chat
# Credits
App Icon
https://www.svgrepo.com/svg/530512/lock

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

    Add Default/all other apps handling killing works
    term Sex to regex because of unisex clothing
    term slavery => remove history podcast
    oral in doctoral
    arsch in nachtbarschaft
    eier => kommt beim kochen vor
    girl => in word check for usernames
    add sexy and hottest for picture search
    scheide -> entscheiden + add k9 to ignore list
    //    "pornography",
    //    "cock", in cocktail
    //    "moan"
    //    "get off",
    //    "grinding",
    //    "sex swing",
