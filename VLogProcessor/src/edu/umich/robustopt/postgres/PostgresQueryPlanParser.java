package edu.umich.robustopt.postgres;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.umich.robustopt.dblogin.QueryPlanParser;


public class PostgresQueryPlanParser extends QueryPlanParser implements Serializable {

	private static final long serialVersionUID = -9127651900130884826L;

	public static List<String> findStructureNamesInRawExplainOutput(String explainOutput) {
		// DY: this is quick-and-dirty solution.
		List<String> namesUsed = new ArrayList<String>();
		
		//TODO: Change this line to fit whatever pattern Postgres uses
		Pattern namesInSquareBracketsPattern = Pattern.compile("\\[(.*?)\\]");

		Matcher m = namesInSquareBracketsPattern.matcher(explainOutput);
		while (m.find()) {
			String candidate = m.group(1);
			//TODO: Change to Postgres equivalent
			if (candidate.contains(PostgresDeployer.DTA_IDENTIFIER))
				namesUsed.add(candidate);
		}
		return namesUsed;
	}


	@Override
	public List<String> searchForPhysicalStructureBaseNamesInRawExplainOutput(
			String rawExplainOutput) {
		List<String> namesUsed = new ArrayList<String>();

		//TODO: Update this line to use POstgres pattern		
		Pattern namesInSquareBracketsPattern = Pattern.compile("\\[(.*?)\\]");

		Matcher m = namesInSquareBracketsPattern.matcher(rawExplainOutput);
		while (m.find()) {
			String candidate = m.group(1);
			//TODO: Change to Postgres equivalent
			if (candidate.contains(PostgresDeployer.DTA_IDENTIFIER))
				namesUsed.add(candidate);
		}
		return namesUsed;
	}

	@Override
	public String extractCanonicalQueryPlan(String rawQueryPlan) {
		// DY: raw query plan in SQL Server is just query plan. It does not contain any additional information such as cost estimates.
		// so it is okay to just return it as is.

		//TODO: Find out if this is the case for Postgres as well or if something must be written
		return rawQueryPlan;
	}

	@Override
	public List<String> searchForPhysicalStructureNamesInCanonicalExplainOutput(
			String queryPlan) {
		List<String> namesUsed = new ArrayList<String>();
		
		//TODO: Change this line to be the pattern postgres uses
		Pattern namesInSquareBracketsPattern = Pattern.compile("\\[(.*?)\\]");

		Matcher m = namesInSquareBracketsPattern.matcher(queryPlan);
		while (m.find()) {
			String candidate = m.group(1);
			//TODO: Change to Postgres equivalent
			if (candidate.contains(PostgresDeployer.DTA_IDENTIFIER))
				namesUsed.add(candidate);
		}
		return namesUsed;
	}

	@Override
	public List<String> searchForPhysicalStructureBaseNamesInCanonicalExplainOutput(
			String queryPlan) {
		List<String> namesUsed = new ArrayList<String>();
		
		//TODO: Change this line to use the Postgres pattern
		Pattern namesInSquareBracketsPattern = Pattern.compile("\\[(.*?)\\]");
		Matcher m = namesInSquareBracketsPattern.matcher(queryPlan);
		while (m.find()) {
			String candidate = m.group(1);
			//TODO: Change to Postgres equivalent
			if (candidate.contains(PostgresDeployer.DTA_IDENTIFIER))
				namesUsed.add(candidate);
		}
		return namesUsed;
	}

	@Override
	public long extractTotalCostsFromRawPlan(String string) {
		return 0;
	}

	@Override
	public List<String> searchForPhysicalStructureNamesInRawExplainOutput(
			String rawExplainOutput) {
		List<String> namesUsed = new ArrayList<String>();
		
		//TODO: Change this line to match Postgres pattern
		Pattern namesInSquareBracketsPattern = Pattern.compile("\\[(.*?)\\]");
		Matcher m = namesInSquareBracketsPattern.matcher(rawExplainOutput);
		while (m.find()) {
			String candidate = m.group(1);
			//TODO: Change to Postgres equivalent
			if (candidate.contains(PostgresDeployer.DTA_IDENTIFIER))
				namesUsed.add(candidate);
		}
		return namesUsed;
	}
}
