package com.guitar.instructor.daos;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table ARTIST.
 */
public class Artist {

    private String id;
    private String name;
    private String unsigned_name;
    private String description;
    private Boolean is_composer;
    private Boolean is_guru;
    private Boolean is_singer;
    private String update_date;

    public Artist() {
    }

    public Artist(String id) {
        this.id = id;
    }

    public Artist(String id, String name, String unsigned_name, String description, Boolean is_composer, Boolean is_guru, Boolean is_singer, String update_date) {
        this.id = id;
        this.name = name;
        this.unsigned_name = unsigned_name;
        this.description = description;
        this.is_composer = is_composer;
        this.is_guru = is_guru;
        this.is_singer = is_singer;
        this.update_date = update_date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUnsigned_name() {
        return unsigned_name;
    }

    public void setUnsigned_name(String unsigned_name) {
        this.unsigned_name = unsigned_name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getIs_composer() {
        return is_composer;
    }

    public void setIs_composer(Boolean is_composer) {
        this.is_composer = is_composer;
    }

    public Boolean getIs_guru() {
        return is_guru;
    }

    public void setIs_guru(Boolean is_guru) {
        this.is_guru = is_guru;
    }

    public Boolean getIs_singer() {
        return is_singer;
    }

    public void setIs_singer(Boolean is_singer) {
        this.is_singer = is_singer;
    }

    public String getUpdate_date() {
        return update_date;
    }

    public void setUpdate_date(String update_date) {
        this.update_date = update_date;
    }

}
