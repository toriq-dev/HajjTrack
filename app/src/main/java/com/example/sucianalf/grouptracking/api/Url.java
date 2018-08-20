package com.example.sucianalf.grouptracking.api;

public class Url {
    public static String URL = "http://sucianalf.web.id/index.php/api/";

    public static class FunctionName {
        public final static String INSERT_ONE_SIGNAL_ID= URL+"insert_one_signal_ID.php";
        public final static String INSERT_NEW_GROUP= URL+"tracking/group";
        public final static String REGISTER_NEW_USER= URL+"tracking/register/";
        public final static String LOGIN= URL+"tracking/login/";
        public final static String INSERT_NEW_GROUP_MEMBER= URL+"tracking/insert_group_member/groupID/";
        public final static String SELECT_RELATED_GROUP = URL+"tracking/select_related_group/";
        public final static String SELECT_RELATED_GROUP_MEMBER = URL+"tracking/select_related_group_member/groupID/";
        public final static String DELETE_GROUP_MEMBER = URL+"tracking/deleteGroup/id/";
        public final static String MEMBER_MARKER = URL+"tracking/member_koordinat/groupID/";
        public final static String UPDATE_LOC = URL+"tracking/koordinat/username/";
        public final static String CHECK_DEST = URL+"tracking/check_destination/groupID/";
        public final static String SET_DEST = URL+"tracking/setDestination/";
        public final static String REMOVE_DEST = URL+"tracking/removeDestination/";
        public final static String GET_PROFIL = URL+"tracking/profile/username/";
        public final static String EDIT_PROFIL = URL+"tracking/edit_profil/";
    }
}
