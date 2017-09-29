package mobi.chouette.dao;

import mobi.chouette.model.compliance.ComplianceCheck;

public interface ComplianceCheckDAO extends GenericDAO<ComplianceCheck> {
	public ComplianceCheck findByCode(final String name);
}
