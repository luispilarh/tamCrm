package com.tam.crm.services;

import com.tam.crm.model.CrmEmail;

public interface EmailService {
	void send(String to, CrmEmail crmEmail);
}
