package com.example.ourpact3.model;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Set;

public class TopicLoader
{
    private String root = "topics/";

    public void setRootFolder(String root)
    {
        if (root != null)
        {
            this.root = root;
        }
    }

    public static class TopicDescriptor
    {
        public String id;
        public String language;
    }

    public ArrayList<TopicDescriptor> getAllLoadableTopics(Context context, Set<String> langCodes)
    {
        if (langCodes == null || langCodes.isEmpty())
        {
            return null;
        }

        AssetManager assets = context.getAssets();
        ArrayList<TopicDescriptor> result = new ArrayList<>();

        try
        {
            String[] allLang = assets.list(root);
            if (allLang == null)
            {
                return null;
            }

            for (String usedLanguage : langCodes)
            {
                for (String lang : allLang)
                {
                    if (lang.equals(usedLanguage))
                    {
                        String[] files = assets.list(root + lang);
                        if (files != null)
                        {
                            for (String file : files)
                            {
                                TopicDescriptor descriptor = new TopicDescriptor();
                                descriptor.id = file;
                                descriptor.language = lang;
                                result.add(descriptor);
                            }
                        }
                    }
                }
            }
        } catch (IOException exp)
        {
            return null;
        }
        return result;
    }

    public Topic loadTopicFile(Context context, TopicDescriptor descriptor)
    {
        AssetManager assets = context.getAssets();
        try
        {
            String fileContent = readAssetFile(assets, root + descriptor.language + "/" + descriptor.id + ".json");
            JSONObject jsonObject = new JSONObject(fileContent);
            return Topic.fromJson(jsonObject);
        } catch (IOException | JSONException e)
        {
            Log.d("TopicLoader", "exception at loading topic" + e);
            return null;
        }
    }

    private String readAssetFile(AssetManager assets, String filePath) throws IOException
    {
        InputStream inputStream = assets.open(filePath);
        byte[] buffer = new byte[inputStream.available()];
        inputStream.read(buffer);
        inputStream.close();
        return new String(buffer);
    }
}
