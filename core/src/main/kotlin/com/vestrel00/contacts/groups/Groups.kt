package com.vestrel00.contacts.groups

import android.content.Context
import com.vestrel00.contacts.ContactsPermissions

/**
 * Provides new [GroupsQuery], [GroupsInsert], and [GroupsUpdate] instances.
 *
 * ## Permissions
 *
 * - Add the "android.permission.READ_CONTACTS" to the AndroidManifest in order to [query].
 * - Add the "android.permission.WRITE_CONTACTS" to the AndroidManifest in order to [insert] and
 * [update].
 *
 * Use [permissions] convenience functions to check for required permissions.
 *
 * ## Deleting Groups
 *
 * Deleting groups, even user-created groups, is currently not supported due to some limitations
 * imposed by the Contacts Provider. For more details, see the DEV_NOTES "Groups; Deletion" section.
 */
interface Groups {

    /**
     * Returns a new [GroupsQuery] instance.
     */
    fun query(): GroupsQuery

    /**
     * Returns a new [GroupsInsert] instance.
     */
    fun insert(): GroupsInsert

    /**
     * Returns a new [GroupsUpdate] instance.
     */
    fun update(): GroupsUpdate

    /*
    /**
     * Returns a new [GroupsDelete] instance.
     */
    fun delete(): GroupsDelete
     */

    /**
     * Returns a new [ContactsPermissions] instance, which provides functions for checking required
     * permissions.
     */
    fun permissions(): ContactsPermissions

    /**
     * Reference to the Application's Context for use in extension functions and external library
     * modules. This is safe to hold on to. Not meant for consumer use.
     */
    val applicationContext: Context
}

@Suppress("FunctionName")
internal fun Groups(context: Context): Groups = GroupsImpl(context.applicationContext)

private class GroupsImpl(override val applicationContext: Context) : Groups {

    override fun query() = GroupsQuery(applicationContext)

    override fun insert() = GroupsInsert(applicationContext)

    override fun update() = GroupsUpdate(applicationContext)

    // override fun delete(): GroupsDelete = GroupsDelete(applicationContext)

    override fun permissions() = ContactsPermissions(applicationContext)
}