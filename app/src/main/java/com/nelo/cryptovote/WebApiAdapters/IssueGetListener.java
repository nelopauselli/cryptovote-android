package com.nelo.cryptovote.WebApiAdapters;

import com.nelo.cryptovote.Domain.Issue;

public interface IssueGetListener {
    void onComplete(Issue issue);
}
