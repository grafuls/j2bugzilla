package com.j2bugzilla.rpc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.j2bugzilla.base.Bug;
import com.j2bugzilla.base.BugFactory;
import com.j2bugzilla.base.BugzillaMethod;

/**
 * This class provides convenience methods for searching for {@link Bug Bugs} on your installation.
 * @author Tom
 *
 */
public class BugSearch implements BugzillaMethod {

	/**
	 * The method Bugzilla will execute via XML-RPC
	 */
	private static final String METHOD_NAME = "Bug.search";
	
	/**
	 * A {@code Map} returned by the XML-RPC method.
	 */
	private Map<Object, Object> hash = new HashMap<Object, Object>();
	
	/**
	 * A {@code Map} used by the XML-RPC method containing the required object parameters.
	 */
	private final Map<Object, Object> params = new HashMap<Object, Object>();
	
	/**
	 * Defines a limit to a search for particular {@link Bug}s.
	 * 
	 * @author Tom
	 *
	 */
	public enum SearchLimiter {
		
		/**
		 * The email of the assignee
		 */
		OWNER("assigned_to"), 
		
		/**
		 * The email of the reporting user
		 */
		REPORTER("reporter"), 
		
		/**
		 * The {@link jbugz.base.Bug.Status} field value
		 */
		STATUS("status"),
		
		/**
		 * The resolution field, if the bug's status is closed. You can search
		 * for all open bugs by searching for a blank resolution.
		 */
		RESOLUTION("resolution"),
		
		/**
		 * The {@link jbugz.base.Bug.Priority} field value
		 */
		PRIORITY("priority"), 
		
		/**
		 * The product affected by this bug
		 */
		PRODUCT("product"),
		
		/**
		 * The component affected by this bug
		 */
		COMPONENT("component"),
		
		/**
		 * The operating system affected by this bug
		 */
		OPERATING_SYSTEM("op_sys"),
		
		/**
		 * The hardware affected by this bug
		 */
		PLATFORM("platform"),
		
		/**
		 * The initial summary comment
		 */
		SUMMARY("summary"),
		
		/**
		 * The version affected by this bug
		 */
		VERSION("version"),
		
		/**
		 * The unique alias for a bug
		 */
		ALIAS("alias"); 
		
		private final String name;
		/**
		 * Creates a new {@link SearchLimiter} with the
		 * designated name
		 * @param name The name Bugzilla expects for this search limiter
		 */
		SearchLimiter(String name) {
			this.name = name;
		}
		/**
		 * Get the name Bugzilla expects for this search limiter
		 * @return A <code>String</code> representing the search limiter
		 */
		String getName() {
			return this.name;
		}
	}
	
	/**
	 * Creates a new {@link BugSearch} object with the appropriate search limit
	 * and query string. 
	 * @param limit What dimension to search {@link Bug Bugs} by in the Bugzilla installation
	 * @param query What to match fields against
	 */
	public BugSearch(SearchLimiter limit, String query) {
		params.put(limit.getName(), query);
	}
	
	/**
	 * Add an additional search limit to the {@link BugSearch}
     * @param limit What dimension to search {@link Bug Bugs} by in the Bugzilla installation
     * @param query What to match fields against
	 */
    public void addQueryParam(SearchLimiter limit, Object query) {
        params.put(limit.getName(), query);
    }
	
	/**
	 * Returns the {@link Bug Bugs} found by the query as a <code>List</code>
	 * @return a {@link List} of {@link Bug Bugs} that match the query and limit
	 */
	public List<Bug> getSearchResults() {
		List<Bug> results = new ArrayList<Bug>();
		/*
		 * The following is messy, but necessary due to how the returned XML document nests
		 * Maps.
		 */
		
		if(hash.containsKey("bugs")) {
			
			//Map<String, Object>[] bugList = (Map<String, Object>[])hash.get("bugs");
			Object[] bugs = (Object[])hash.get("bugs");
			if(bugs.length == 0) { 
				return results; //early return if map is empty
			}
			
			for(Object o : bugs) {
				@SuppressWarnings("unchecked")
				Map<String, Object> bugMap = (HashMap<String, Object>)o;
				Bug bug = new BugFactory().createBug(bugMap);
				results.add(bug);
			}
		}
		return results;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setResultMap(Map<Object, Object> hash) {
		this.hash = hash;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<Object, Object> getParameterMap() {
		return params;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getMethodName() {
		return METHOD_NAME;
	}
}