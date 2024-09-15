package com.example.ourpact3.service;

/**
 * Apps can have special permission, so that user e.g cannot edit/learn them
 */
public enum AppPermission
{
    USER_RW,    //User can edit and read/learn
    USER_IGNORE_LIST,   //App is not processed user can change that to RW
    USER_LOCKED,    //Read only but it should go back to rw
    USER_RO,    // only the developer mode can change it e.g settings, but its processed
    USER_HIDDEN,    //User can not see or edit this
}
