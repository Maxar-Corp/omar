package org.ossim.omar

import org.ossim.omar.Role

/**
 * org.ossim.omar.Role Controller.
 */
class RoleController {

	// the delete, save and update actions only accept POST requests
	def static allowedMethods = [delete: 'POST', save: 'POST', update: 'POST']

	def index = {
		redirect(action: list, params: params)
	}

	def list = {
		if (!params.max) {
			params.max = 10
		}
		[authorityList: Role.list(params)]
	}

	def show = {
		[authority: Role.get(params.id)]
	}

	def delete = {
		def authority = Role.get(params.id)
		if (!authority) {
			flash.message = "Role not found with id ${params.id}"
			redirect(action: list)
			return
		}

		String oldRole = authority.authority
		def rms = Requestmap.findAllByConfigAttributeLike('%' + oldRole + '%')
		rms.each {
			it.configAttribute = it.configAttribute.replace(oldRole, '')
			it.validate()
		}
		authority.delete()
		flash.message = "Role ${params.id} deleted."
		redirect(action: list)
	}

	def edit = {
		def authority = Role.get(params.id)
		if (!authority) {
			flash.message = "Role not found with id ${params.id}"
			redirect(action: list)
			return
		}

		[authority: authority]
	}

	/**
	 * Authority update action. When updating an existing authority instance, the requestmaps which contain
	 * them should also be updated.
	 */
	def update = {

		def authority = Role.get(params.id)
		if (!authority) {
			flash.message = "Role not found with id ${params.id}"
			redirect(action: edit, id: params.id)
			return
		}

		String oldRole = authority.authority
		authority.properties = params
		String role = params.authority
		authority.authority = 'ROLE_' + role.toUpperCase()
		String newRole = authority.authority
		def rms = Requestmap.findAllByConfigAttributeLike('%' + oldRole + '%')
		rms.each {
			it.configAttribute = it.configAttribute.replace(oldRole, newRole)
			it.validate()
		}
		if (authority.save()) {
			redirect(action: show, id: authority.id)
		}
		else {
			render(view: 'edit', model: [authority: authority])
		}
	}

	def create = {
		def authority = new Role()
		authority.authority = ''
		authority.properties = params
		[authority: authority]
	}

	/**
	 * Authority save action.
	 */
	def save = {

		def authority = new Role()
		String au = params.authority
		authority.properties = params
		//here translate user's input to the required format
		authority.authority = 'ROLE_' + au.toUpperCase()
		if (authority.save()) {
			redirect(action: show, id: authority.id)
		}
		else {
			render(view: 'create', model: [authority: authority])
		}
	}
}
