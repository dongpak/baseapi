/*
 */
package com.churchclerk.baseapi.model;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 *
 */
public class ApiCaller {

    public enum Role {
        SUPER, ADMIN, CLERK, OFFICIAL, MEMBER, NONMEMBER
    }

    private String      userid;
    private Set<Role>   roles;
    private Set<String> memberOf;   // first element being the primary


    /**
     *
     */
    public ApiCaller(String id, String roles) {
        String[]    ids = id.split("\\|");

        parseUserId(ids);
        parseRoles(roles.split(","));
        parseMemberOf(ids);
    }

    private void parseUserId(String[] ids) {
        userid = ids[0];
    }

    private void parseRoles(String[] roles) {
        this.roles = new HashSet<Role>();

        for (String role : roles) {
            this.roles.add(Role.valueOf(role));
        }
    }

    private void parseMemberOf(String[] ids) {
        memberOf = new HashSet<String>();

        if (ids.length > 1) {
            for (int i = 1; i < ids.length; i++) {
                memberOf.add(ids[i]);
            }
        }
    }

    public String getUserid() {
        return userid;
    }

    public Set<Role> getRoles() {
        return Collections.unmodifiableSet(roles);
    }

    public Set<String> getMemberOf() {
        return Collections.unmodifiableSet(memberOf);
    }

    /**
     *
     * @return
     */
    public boolean hasSuperRole() {
        return roles.contains(Role.SUPER);
    }

    /**
     *
     * @return
     */
    public boolean hasAdminRole() {
        return roles.contains(Role.ADMIN);
    }

    /**
     *
     * @return
     */
    public boolean hasClerkRole() {
        return roles.contains(Role.CLERK);
    }

    /**
     *
     * @return
     */
    public boolean hasOfficialRole() {
        return roles.contains(Role.CLERK) || roles.contains(Role.OFFICIAL);
    }

    /**
     *
     * @return
     */
    public boolean hasMemberRole() {
        return roles.contains(Role.MEMBER);
    }

    /**
     *
     * @param id
     * @param roles
     * @return
     */
    private boolean operationAllowed(String id, Role[] roles) {
        if (hasSuperRole()) {
            return true;
        }

        for (Role role : roles) {
            if (this.roles.contains(role)) {
                if (id == null) {
                    return true;
                }

                return memberOf.contains(id);
            }
        }

        return false;
    }

    /**
     *
     * @param id
     * @return
     */
    public boolean readAllowed(String id, Role[] readRoles) {
        return operationAllowed(id, readRoles);
    }

    /**
     *
     * @param id
     * @return
     */
    public boolean createAllowed(String id, Role[] createRoles) {
        return operationAllowed(id, createRoles);
    }

    /**
     *
     * @param id
     * @return
     */
    public boolean updateAllowed(String id, Role[] updateRoles) {
        return operationAllowed(id, updateRoles);
    }

    /**
     *
     * @param id
     * @return
     */
    public boolean deleteAllowed(String id, Role[] deleteRoles) {
        return operationAllowed(id, deleteRoles);
    }

    @Override
    public String toString() {
        return userid;
    }
}
