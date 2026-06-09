package com.team2.onboarding.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class DBService {

    public Object getDashboard(String companyId) {
        return null;
    }

    public Object getMembers(String companyId) {
        return null;
    }

    public Object getDeadlines(String companyId) {
        return null;
    }

    public Object getDocuments(String companyId) {
        return null;
    }
}
