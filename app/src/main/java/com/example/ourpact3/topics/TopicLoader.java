package com.example.ourpact3.topics;

import android.content.Context;
import android.content.res.AssetManager;

import com.example.ourpact3.util.JSONCommentRemover;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Set;

public class TopicLoader
{
    private String root = "topics/";

    public void setRootFolder(String root)
    {
        if (root != null && root.endsWith("/"))
        {
            this.root = root;
        }
    }

    public static class TopicDescriptor
    {
        public String file_name;
        public String language;
    }

    public ArrayList<TopicDescriptor> getAllLoadableTopics(Context context, Set<String> langCodes) throws TopicLoaderException
    {
        if (langCodes == null || langCodes.isEmpty())
        {
            throw new TopicLoaderException("getAllLoadableTopics is called without a correct language set");
        }

        AssetManager assets = context.getAssets();
        ArrayList<TopicDescriptor> result = new ArrayList<>();

        try
        {
            String[] allLang = assets.list(root);
            if (allLang == null || allLang.length == 0)
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
                                descriptor.file_name = file;
                                descriptor.language = lang;
                                result.add(descriptor);
                            }
                        }
                    }
                }
            }
        } catch (IOException exp)
        {
            throw new TopicLoaderException(exp.getMessage());
        }
        return result;
    }

    public Topic loadTopicFile(Context context, TopicDescriptor descriptor) throws TopicLoaderException
    {
        AssetManager assets = context.getAssets();
        try
        {
            String fileContent = readAssetFile(assets, root + descriptor.language + "/" + descriptor.file_name);
            String fileWithoutComments = JSONCommentRemover.removeComments(fileContent);
            JSONObject jsonObject = new JSONObject(fileWithoutComments);
            Topic topic = Topic.fromJson(jsonObject);
            if (!descriptor.file_name.contains(topic.getTopicId()) || !Objects.equals(topic.getLanguage(), descriptor.language))
            {
                throw new TopicLoaderException("topic descriptor " + descriptor.file_name + " in folder "+ descriptor.language + " mismatch with loaded topic " + topic.getTopicUID());
            }
            return topic;
        } catch (IOException | JSONException e)
        {
            throw new TopicLoaderException("could not load topic file " + descriptor.toString());
        }
    }

    private String readAssetFile(AssetManager assets, String filePath) throws IOException
    {
        InputStream inputStream = assets.open(filePath);
        byte[] buffer = new byte[inputStream.available()];
        int bytesRead = inputStream.read(buffer);
        inputStream.close();

        if (bytesRead != buffer.length)
        {
            // handle the case where not all bytes were read
            // you can either retry reading, or throw an exception
            throw new IOException("Failed to read entire file");
        }

        return new String(buffer);
    }
}
