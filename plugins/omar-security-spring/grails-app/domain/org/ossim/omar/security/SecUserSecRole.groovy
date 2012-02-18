package org.ossim.omar.security

import org.apache.commons.lang.builder.HashCodeBuilder

class SecUserSecRole implements Serializable {

	SecUser authUser
	SecRole role

	boolean equals(other) {
		if (!(other instanceof SecUserSecRole)) {
			return false
		}

		other.authUser?.id == authUser?.id &&
			other.role?.id == role?.id
	}

	int hashCode() {
		def builder = new HashCodeBuilder()
		if (authUser) builder.append(authUser.id)
		if (role) builder.append(role.id)
		builder.toHashCode()
	}

	static SecUserSecRole get(long authUserId, long roleId) {
		find 'from SecUserSecRole where authUser.id=:authUserId and role.id=:roleId',
			[authUserId: authUserId, roleId: roleId]
	}

	static SecUserSecRole create(SecUser authUser, SecRole role, boolean flush = false) {
		new SecUserSecRole(authUser: authUser, role: role).save(flush: flush, insert: true)
	}

	static boolean remove(SecUser authUser, SecRole role, boolean flush = false) {
		SecUserSecRole instance = SecUserSecRole.findByAuthUserAndRole(authUser, role)
		instance ? instance.delete(flush: flush) : false
	}

	static void removeAll(SecUser authUser) {
		executeUpdate 'DELETE FROM SecUserSecRole WHERE authUser=:authUser', [authUser: authUser]
	}

	static void removeAll(SecRole role) {
		executeUpdate 'DELETE FROM SecUserSecRole WHERE role=:role', [role: role]
	}

	static mapping = {
		id composite: ['role', 'authUser']
		version false
	}
}
