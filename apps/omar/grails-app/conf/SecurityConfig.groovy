security {

	active = true

	// change to true to use OpenID authentication
	useOpenId = false

	/* 
	// LDAP Configuration
	
	providerNames = ['daoAuthenticationProvider',
	                             'ldapAuthProvider']
	useLdap = true
	ldapRetrieveDatabaseRoles = true
	ldapRetrieveGroupRoles = false
	ldapServer = 'ldap://sles11-ldap-server'
	ldapManagerDn = 'cn=Administrator,dc=otd,dc=radiantblue,dc=com'
	ldapManagerPassword = 'omarldap'
	ldapSearchBase = 'ou=people,dc=otd,dc=radiantblue,dc=com'
	ldapSearchFilter = '(uid={0})'
	ldapGroupSearchBase = 'ou=groups,dc=otd,dc=radiantblue,dc=com'
	ldapGroupSearchFilter = 'uniquemember={0}'
	*/

	algorithm = 'MD5' 
	//use Base64 text ( true or false )
	encodeHashAsBase64 = false
	errorPage = null

	/** login user domain class name and fields */
	loginUserDomainClass = "org.ossim.omar.AuthUser"
	userName = 'username'
	password = 'passwd'
	enabled = 'enabled'
	relationalAuthorities = 'authorities'

	/*
	 * You can specify method to retrieve the roles. (you need to set relationalAuthorities = null)
	 */
	// getAuthoritiesMethod = null //'getMoreAuthorities'

	/**
	 * Authority domain class authority field name
	 * authorityFieldInList
	 */
	authorityDomainClass = "org.ossim.omar.Role"
	authorityField = 'authority'

	/** use RequestMap from DomainClass */
	useRequestMapDomainClass = true
	/** org.ossim.omar.Requestmap domain class (if useRequestMapDomainClass = true) */
	requestMapClass = "org.ossim.omar.Requestmap"
	requestMapPathField = 'url'
	requestMapConfigAttributeField = 'configAttribute'

	/** 
	 * if useRequestMapDomainClass is false, set request map pattern in string
	 * see example below
	 */

	/*
	requestMapString = """
		CONVERT_URL_TO_LOWERCASE_BEFORE_COMPARISON
		PATTERN_TYPE_APACHE_ANT

		/login/**=IS_AUTHENTICATED_ANONYMOUSLY
		/admin/**=ROLE_USER
		/book/test/**=IS_AUTHENTICATED_FULLY
		/book/**=ROLE_SUPERVISOR
		/**=IS_AUTHENTICATED_ANONYMOUSLY
	"""
	*/

	/**
	 * To use email notification for user registration, set the following userMail to
	 * true and config your mail settings.Note you also need to run the script
	 * grails generate-registration.
	 */
	
	if (grailsApplication.config.login.registration.userVerification == 'email')
	{
	  useMail = true
	  println "useMail true"
	}
	else
	{
	  useMail = false
	  println "useMail false"
	}
		
	//mailHost = 'localhost'
	//mailUsername = 'user@localhost'
	//mailPassword = 'sungod'
	//mailProtocol = 'smtp'
	//mailFrom = 'user@localhost'

	/** AJAX request header */
	//ajaxHeader = 'X-Requested-With'
  
	/** default user's role for user registration */
	defaultRole = 'ROLE_USER'

	/** use basicProcessingFilter */
	basicProcessingFilter = false
	/** use switchUserProcessingFilter */
	switchUserProcessingFilter = false
}
