package com.nelo.cryptovote.WebApiAdapters;

import com.nelo.cryptovote.Domain.Urn;

public interface UrnListListener {
    void onComplete(Urn[] urns);
}
