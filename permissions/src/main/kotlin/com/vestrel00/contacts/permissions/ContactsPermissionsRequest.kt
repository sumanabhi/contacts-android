package com.vestrel00.contacts.permissions

import android.content.Context
import com.vestrel00.contacts.*
import com.vestrel00.contacts.permissions.accounts.requestGetAccountsPermission

/**
 * If [ContactsPermissions.READ_PERMISSION] is not yet granted, suspends the current coroutine,
 * requests for the permission, and then returns a new [Query] instance.
 *
 * If permission is already granted, then immediately returns a new [Query] instance.
 */
suspend fun Contacts.queryWithPermission(): Query {
    val permissions = permissions()
    if (!permissions.canQuery()) {
        applicationContext.requestReadPermission()
    }

    return query()
}

/**
 * If [ContactsPermissions.READ_PERMISSION] is not yet granted, suspends the current coroutine,
 * requests for the permission, and then returns a new [GeneralQuery] instance.
 *
 * If permission is already granted, then immediately returns a new [GeneralQuery] instance.
 */
suspend fun Contacts.generalQueryWithPermission(): GeneralQuery {
    val permissions = permissions()
    if (!permissions.canQuery()) {
        applicationContext.requestReadPermission()
    }

    return generalQuery()
}

/**
 * If [ContactsPermissions.WRITE_PERMISSION] and
 * [com.vestrel00.contacts.accounts.AccountsPermissions.GET_ACCOUNTS_PERMISSION] are not yet
 * granted, suspends the current coroutine, requests for the permissions, and then returns a new
 * [Insert] instance.
 *
 * If permissions are already granted, then immediately returns a new [Insert] instance.
 */
suspend fun Contacts.insertWithPermission(): Insert {
    val permissions = permissions()
    if (!permissions.canInsert()) {
        applicationContext.requestWritePermission()
        applicationContext.requestGetAccountsPermission()
    }

    return insert()
}

/**
 * If [ContactsPermissions.WRITE_PERMISSION] is not yet granted, suspends the current coroutine,
 * requests for the permissions, and then returns a new [Update] instance.
 *
 * If permissions are already granted, then immediately returns a new [Update] instance.
 */
suspend fun Contacts.updateWithPermission(): Update {
    val permissions = permissions()
    if (!permissions.canUpdateDelete()) {
        applicationContext.requestWritePermission()
    }

    return update()
}

/**
 * If [ContactsPermissions.WRITE_PERMISSION] is not yet granted, suspends the current coroutine,
 * requests for the permission, and then returns a new [Delete] instance.
 *
 * If permission is already granted, then immediately returns a new [Delete] instance.
 */
suspend fun Contacts.deleteWithPermission(): Delete {
    val permissions = permissions()
    if (!permissions.canUpdateDelete()) {
        applicationContext.requestWritePermission()
    }

    return delete()
}

/**
 * Requests the [ContactsPermissions.READ_PERMISSION]. The current coroutine is suspended until the
 * user either grants or denies the permission request.
 *
 * Returns true if permission is granted. False otherwise.
 */
suspend fun Context.requestReadPermission(): Boolean =
    requestContactsPermission(ContactsPermissions.READ_PERMISSION)

/**
 * Requests the [ContactsPermissions.WRITE_PERMISSION]. The current coroutine is suspended until
 * the user either grants or denies the permissions request.
 *
 * Returns true if permission is granted. False otherwise.
 */
suspend fun Context.requestWritePermission(): Boolean =
    requestContactsPermission(ContactsPermissions.WRITE_PERMISSION)

private suspend fun Context.requestContactsPermission(permission: String): Boolean =
    requestPermission(
        permission,
        this,
        R.string.contacts_request_permission_title,
        R.string.contacts_request_permission_description
    )