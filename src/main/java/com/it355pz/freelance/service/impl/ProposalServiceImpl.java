package com.it355pz.freelance.service.impl;

import com.it355pz.freelance.repository.ApplicationData;
import com.it355pz.freelance.service.ProposalService;
import org.springframework.stereotype.Service;

@Service
public class ProposalServiceImpl implements ProposalService {

    private final ApplicationData data;

    public ProposalServiceImpl(ApplicationData data) {
        this.data = data;
    }

    @Override
    public long count() {
        return data.getProposals().size();
    }
}
