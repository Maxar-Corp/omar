import org.codehaus.groovy.grails.commons.ApplicationHolder

security {

	active = true

	// change to true to use OpenID authentication
	useOpenId = false

   /*
    * To use LDAP for user authentication uncomment the following block and
    * set the variables according to your environment.
    */

    /*
    providerNames = ['daoAuthenticationProvider','ldapAuthProvider']
    useLdap = true
	ldapRetrieveDatabaseRoles = true
	ldapRetrieveGroupRoles = false
	ldapServer = 'ldap://your-ldap-server-name'
	ldapManagerDn = 'cn=Administrator,dc=sample,dc=domain,dc=com'
	ldapManagerPassword = 'password'
	ldapSearchBase = 'ou=people,dc=sample,dc=domain,dc=com'
	ldapSearchFilter = '(uid={0})'
	ldapGroupSearchBase = 'ou=groups,dc=sample,dc=domain,dc=com'
	ldapGroupSearchFilter = 'uniquemember={0}'
    */

  	/*
  	 * To use email validation for user registration, uncomment the following block
  	 * and set the variables according to your your environment.
    /*

    /*
	useMail = ApplicationHolder.application.config.login.registration.useMail
	mailHost = 'localhost'
	mailUsername = 'omar@localhost'
	mailPassword = 'password'
	mailProtocol = 'smtp'
	mailFrom = 'omar@localhost'
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

	/** AJAX request header */
	//ajaxHeader = 'X-Requested-With'
  
	/** default user's role for user registration */
	defaultRole = 'ROLE_USER'

	/** use basicProcessingFilter */
	basicProcessingFilter = false
	/** use switchUserProcessingFilter */
	switchUserProcessingFilter = false
}