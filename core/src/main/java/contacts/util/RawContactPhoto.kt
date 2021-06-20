package contacts.util

import android.content.ContentProviderOperation.newDelete
import android.content.ContentUris
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.provider.ContactsContract.RawContacts
import contacts.*
import contacts.entities.MimeType
import contacts.entities.Photo
import contacts.entities.RawContactEntity
import contacts.entities.cursor.photoCursor
import contacts.entities.operation.withSelection
import contacts.entities.table.ProfileUris
import contacts.entities.table.Table
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream

// region GET PHOTO

/**
 * Returns the full-sized photo as an [InputStream]. Returns null if a photo has not yet been set.
 *
 * It is up to the caller to close the [InputStream].
 *
 * Supports profile and non-profile RawContacts.
 *
 * ## Permissions
 *
 * This requires the [ContactsPermissions.READ_PERMISSION].
 *
 * ## Thread Safety
 *
 * This should be called in a background thread to avoid blocking the UI thread.
 */
// [ANDROID X] @WorkerThread (not using annotation to avoid dependency on androidx.annotation)
fun RawContactEntity.photoInputStream(context: Context): InputStream? {
    val rawContactId = id

    if (!ContactsPermissions(context).canQuery() || rawContactId == null) {
        return null
    }

    val photoUri = Uri.withAppendedPath(
        // This is also used to set Profile photos along with non-Profile photos.
        ContentUris.withAppendedId(RawContacts.CONTENT_URI, rawContactId),
        RawContacts.DisplayPhoto.CONTENT_DIRECTORY
    )

    var inputStream: InputStream? = null
    try {
        val fd = context.contentResolver.openAssetFileDescriptor(photoUri, "r")
        inputStream = fd?.createInputStream()
    } finally {
        return inputStream
    }
}

/**
 * Returns the full-sized photo as a [ByteArray]. Returns null if a photo has not yet been set.
 *
 * Supports profile and non-profile RawContacts.
 *
 * ## Permissions
 *
 * This requires the [ContactsPermissions.READ_PERMISSION].
 *
 * ## Thread Safety
 *
 * This should be called in a background thread to avoid blocking the UI thread.
 */
// [ANDROID X] @WorkerThread (not using annotation to avoid dependency on androidx.annotation)
fun RawContactEntity.photoBytes(context: Context): ByteArray? = photoInputStream(context)?.apply {
    it.readBytes()
}

/**
 * Returns the full-sized photo as a [Bitmap]. Returns null if a photo has not yet been set.
 *
 * Supports profile and non-profile RawContacts.
 *
 * ## Permissions
 *
 * This requires the [ContactsPermissions.READ_PERMISSION].
 *
 * ## Thread Safety
 *
 * This should be called in a background thread to avoid blocking the UI thread.
 */
// [ANDROID X] @WorkerThread (not using annotation to avoid dependency on androidx.annotation)
fun RawContactEntity.photoBitmap(context: Context): Bitmap? = photoInputStream(context)?.apply {
    BitmapFactory.decodeStream(it)
}

/**
 * Returns the full-sized photo as a [BitmapDrawable]. Returns null if a photo has not yet been set.
 *
 * Supports profile and non-profile RawContacts.
 *
 * ## Permissions
 *
 * This requires the [ContactsPermissions.READ_PERMISSION].
 *
 * ## Thread Safety
 *
 * This should be called in a background thread to avoid blocking the UI thread.
 */
// [ANDROID X] @WorkerThread (not using annotation to avoid dependency on androidx.annotation)
fun RawContactEntity.photoBitmapDrawable(context: Context): BitmapDrawable? =
    photoInputStream(context)?.apply {
        BitmapDrawable(context.resources, it)
    }

internal inline fun <T> InputStream.apply(block: (InputStream) -> T): T {
    val t = block(this)
    close()
    return t
}

// endregion

// region GET PHOTO THUMBNAIL

/**
 * Returns the photo thumbnail as an [InputStream]. Returns null if a photo has not yet been set.
 *
 * It is up to the caller to close the [InputStream].
 *
 * Supports profile and non-profile RawContacts.
 *
 * ## Permissions
 *
 * This requires the [ContactsPermissions.READ_PERMISSION].
 *
 * ## Thread Safety
 *
 * This should be called in a background thread to avoid blocking the UI thread.
 */
// [ANDROID X] @WorkerThread (not using annotation to avoid dependency on androidx.annotation)
fun RawContactEntity.photoThumbnailInputStream(context: Context): InputStream? {
    val rawContactId = id

    if (!ContactsPermissions(context).canQuery() || rawContactId == null) {
        return null
    }

    return context.contentResolver.query(
        if (isProfile) ProfileUris.DATA.uri else Table.Data.uri,
        Include(Fields.Photo.PhotoThumbnail),
        (Fields.RawContact.Id equalTo rawContactId)
                and (Fields.MimeType equalTo MimeType.Photo)
    ) {
        val photoThumbnail = it.getNextOrNull { it.photoCursor().photoThumbnail }
        photoThumbnail?.let(::ByteArrayInputStream)
    }
}

/**
 * Returns the photo thumbnail as a [ByteArray]. Returns null if a photo has not yet been set.
 *
 * Supports profile and non-profile RawContacts.
 *
 * ## Permissions
 *
 * This requires the [ContactsPermissions.READ_PERMISSION].
 *
 * ## Thread Safety
 *
 * This should be called in a background thread to avoid blocking the UI thread.
 */
// [ANDROID X] @WorkerThread (not using annotation to avoid dependency on androidx.annotation)
fun RawContactEntity.photoThumbnailBytes(context: Context): ByteArray? =
    photoThumbnailInputStream(context)?.apply {
        it.readBytes()
    }

/**
 * Returns the photo thumbnail as a [Bitmap]. Returns null if a photo has not yet been set.
 *
 * Supports profile and non-profile RawContacts.
 *
 * ## Permissions
 *
 * This requires the [ContactsPermissions.READ_PERMISSION].
 *
 * ## Thread Safety
 *
 * This should be called in a background thread to avoid blocking the UI thread.
 */
// [ANDROID X] @WorkerThread (not using annotation to avoid dependency on androidx.annotation)
fun RawContactEntity.photoThumbnailBitmap(context: Context): Bitmap? =
    photoThumbnailInputStream(context)?.apply {
        BitmapFactory.decodeStream(it)
    }

/**
 * Returns the photo thumbnail as a [BitmapDrawable]. Returns null if a photo has not yet been set.
 *
 * Supports profile and non-profile RawContacts.
 *
 * ## Permissions
 *
 * This requires the [ContactsPermissions.READ_PERMISSION].
 *
 * ## Thread Safety
 *
 * This should be called in a background thread to avoid blocking the UI thread.
 */
// [ANDROID X] @WorkerThread (not using annotation to avoid dependency on androidx.annotation)
fun RawContactEntity.photoThumbnailBitmapDrawable(context: Context): BitmapDrawable? =
    photoThumbnailInputStream(context)?.apply {
        BitmapDrawable(context.resources, it)
    }

// endregion

// region SET PHOTO

/**
 * Sets the photo of this [RawContactEntity]. If a photo already exists, it will be overwritten.
 * The Contacts Provider automatically creates a downsized version of this as the thumbnail.
 *
 * If this [RawContactEntity] is the only one that make up a [contacts.entities.ContactEntity], then
 * the photo set here will also be used by the Contacts Provider as the contact photo. Otherwise, it
 * may or may not be the photo picked by the Contacts Provider as the contact photo.
 *
 * Returns true if the operation succeeds.
 *
 * Supports profile and non-profile RawContacts.
 *
 * ## Permissions
 *
 * This requires the [ContactsPermissions.WRITE_PERMISSION].
 *
 * ## Thread Safety
 *
 * This should be called in a background thread to avoid blocking the UI thread.
 *
 * ## Developer notes
 *
 * The function body is mostly taken from the sample code from the [RawContacts.DisplayPhoto] class
 * documentation.
 */
// [ANDROID X] @WorkerThread (not using annotation to avoid dependency on androidx.annotation)
fun RawContactEntity.setPhoto(context: Context, photoBytes: ByteArray): Boolean =
    doSetPhoto(context, photoBytes)

/**
 * See [RawContactEntity.setPhoto].
 */
// [ANDROID X] @WorkerThread (not using annotation to avoid dependency on androidx.annotation)
fun RawContactEntity.setPhoto(context: Context, photoInputStream: InputStream): Boolean =
    setPhoto(context, photoInputStream.readBytes())

/**
 * See [RawContactEntity.setPhoto].
 */
// [ANDROID X] @WorkerThread (not using annotation to avoid dependency on androidx.annotation)
fun RawContactEntity.setPhoto(context: Context, photoBitmap: Bitmap): Boolean =
    setPhoto(context, photoBitmap.bytes())

/**
 * See [RawContactEntity.setPhoto].
 */
// [ANDROID X] @WorkerThread (not using annotation to avoid dependency on androidx.annotation)
fun RawContactEntity.setPhoto(context: Context, photoDrawable: BitmapDrawable): Boolean =
    setPhoto(context, photoDrawable.bitmap.bytes())

/**
 * Performs the actual setting of the photo. Only the [RawContactEntity.id] is required to be
 * non-null for the operation.
 */
internal fun RawContactEntity.doSetPhoto(context: Context, photoBytes: ByteArray): Boolean {
    val rawContactId = id

    if (!ContactsPermissions(context).canUpdateDelete() || rawContactId == null) {
        return false
    }

    var isSuccessful = false
    try {
        val photoUri = Uri.withAppendedPath(
            // This is also used to set Profile photos along with non-Profile photos.
            ContentUris.withAppendedId(RawContacts.CONTENT_URI, rawContactId),
            RawContacts.DisplayPhoto.CONTENT_DIRECTORY
        )

        // Didn't want to force unwrap because I'm trying to keep the codebase free of it.
        // I wanted to fold the if-return using ?: but it results in a lint error about unreachable
        // code (it's not unreachable).
        val fd = context.contentResolver.openAssetFileDescriptor(photoUri, "rw")
        if (fd != null) {
            val os = fd.createOutputStream()

            os.write(photoBytes)

            os.close()
            fd.close()

            isSuccessful = true
        }
    } finally {
        if (isSuccessful) {
            // Assume that the photo Data row has been created and inject a photo instance into the
            // entity so that it will not be marked as blank if it has no other Data rows.
            // We could make a query here just to make sure but we won't to save time and CPU. Also,
            // I don't know if the Data row is immediately created at this point.
            photo = Photo()
        }

        return isSuccessful
    }
}

internal fun Bitmap.bytes(): ByteArray {
    val outputStream = ByteArrayOutputStream()
    compress(Bitmap.CompressFormat.PNG, 100, outputStream)
    return outputStream.toByteArray()
}

// endregion

// region REMOVE PHOTO

/**
 * Removes the photo of this [RawContactEntity], if one exists.
 *
 * If this [RawContactEntity] is the only one that make up a [contacts.entities.ContactEntity], then
 * the contact photo will also be removed.
 * Otherwise, it may or may not affect the contact photo.
 *
 * Returns true if the operation succeeds.
 *
 * Supports profile and non-profile RawContacts.
 *
 * ## Permissions
 *
 * This requires the [ContactsPermissions.WRITE_PERMISSION].
 *
 * ## Thread Safety
 *
 * This should be called in a background thread to avoid blocking the UI thread.
 */
// [ANDROID X] @WorkerThread (not using annotation to avoid dependency on androidx.annotation)
fun RawContactEntity.removePhoto(context: Context): Boolean {
    val rawContactId = id

    if (!ContactsPermissions(context).canUpdateDelete() || rawContactId == null) {
        return false
    }

    val isSuccessful = context.contentResolver.applyBatch(
        newDelete(if (isProfile) ProfileUris.DATA.uri else Table.Data.uri)
            .withSelection(
                (Fields.RawContact.Id equalTo rawContactId)
                        and (Fields.MimeType equalTo MimeType.Photo)
            )
            .build()
    ) != null

    if (isSuccessful) {
        // Assume that the photo Data row has been deleted and remove the photo instance from the
        // entity so that it will be marked as blank if it has no other Data rows.
        // We could make a query here just to make sure but we won't to save time and CPU. Also,
        // I don't know if the Data row is immediately deleted at this point.
        photo = null
    }

    return isSuccessful
}

// endregion