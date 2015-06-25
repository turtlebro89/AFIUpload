package llavender.afiupload;

import android.net.Uri;

import com.parse.ParseObject;


import java.util.ArrayList;

/**
 * Singleton class to hold variables for the entire activity
 */
public class Holder {

    private static Holder holder = null;
    private static ArrayList<ParseObject> clients = new ArrayList<>();
    private static ArrayList<ParseObject> policies = new ArrayList<>();;
    private static ParseObject policy;
    private static ArrayList<Uri> images = new ArrayList<>();;
    private static ArrayList<String> comments = new ArrayList<>();;
    private static ArrayList<ParseObject> parseObjects = new ArrayList<>();;
    private static ArrayList<String> imageUrls = new ArrayList<>();;
    private static ArrayList<String> commentsList = new ArrayList<>();;


    protected Holder(){    }

    public static Holder getInstance(){
        if(holder == null){
            holder = new Holder();
        }
        return holder;
    }

    public static ArrayList<ParseObject> getClients() {
        return clients;
    }

    public static ArrayList<ParseObject> getPolicies() {
        return policies;
    }

    public static ParseObject getPolicy() {
        return policy;
    }

    public static void setClients(ArrayList<ParseObject> clients) {
        Holder.clients = clients;
    }

    public static void setPolicies(ArrayList<ParseObject> policies) {
        Holder.policies = policies;
    }

    public static void setPolicy(ParseObject policy) {
        Holder.policy = policy;
    }

    public static ArrayList<Uri> getImages() {
        return images;
    }

    public static void setImages(ArrayList<Uri> images) {
        Holder.images = images;
    }

    public static void addImages(ArrayList<Uri> images) {
        Holder.images.addAll(images);
    }

    public static ArrayList<ParseObject> getParseObjects() {
        return parseObjects;
    }

    public static void setComments(ArrayList<String> comments) {
        Holder.comments = comments;
    }

    public static void putInComments(String comment, int position){
        Holder.comments.set(position, comment);
    }

    public static void setImageUrls(ArrayList<String> imageUrls) {
        Holder.imageUrls = imageUrls;
    }

    public static void setParseObjects(ArrayList<ParseObject> parseObjects) {
        Holder.parseObjects = parseObjects;
    }

    public static ArrayList<String> getCommentsList() {
        return commentsList;
    }

    public static void setCommentsList(ArrayList<String> commentsList) {
        Holder.commentsList = commentsList;
    }

    public static void putInCommentsList(String comment, int position){
        Holder.commentsList.set(position, comment);
    }

}
