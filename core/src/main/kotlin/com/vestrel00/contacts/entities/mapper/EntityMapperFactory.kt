package com.vestrel00.contacts.entities.mapper

import com.vestrel00.contacts.AbstractDataField
import com.vestrel00.contacts.ContactsField
import com.vestrel00.contacts.GroupsField
import com.vestrel00.contacts.RawContactsField
import com.vestrel00.contacts.entities.*
import com.vestrel00.contacts.entities.cursor.*

// region EntityCursor<AbstractDataField>

internal fun EntityCursor<AbstractDataField>.addressMapper(): EntityMapper<Address> =
    AddressMapper(addressCursor())

internal fun EntityCursor<AbstractDataField>.dataContactsMapper(isProfile: Boolean):
        EntityMapper<Contact> = ContactMapper(
    dataContactsCursor(), dataContactsOptionsMapper(), isProfile
)

internal fun EntityCursor<AbstractDataField>.emailMapper(): EntityMapper<Email> =
    EmailMapper(emailCursor())

internal fun EntityCursor<AbstractDataField>.eventMapper(): EntityMapper<Event> =
    EventMapper(eventCursor())

internal fun EntityCursor<AbstractDataField>.groupMembershipMapper(): EntityMapper<GroupMembership> =
    GroupMembershipMapper(groupMembershipCursor())

internal fun EntityCursor<AbstractDataField>.imMapper(): EntityMapper<Im> = ImMapper(imCursor())

internal fun EntityCursor<AbstractDataField>.nameMapper(): EntityMapper<Name> =
    NameMapper(nameCursor())

internal fun EntityCursor<AbstractDataField>.nicknameMapper(): EntityMapper<Nickname> =
    NicknameMapper(nicknameCursor())

internal fun EntityCursor<AbstractDataField>.noteMapper(): EntityMapper<Note> =
    NoteMapper(noteCursor())

internal fun EntityCursor<AbstractDataField>.dataContactsOptionsMapper(): EntityMapper<Options> =
    OptionsMapper(dataContactsOptionsCursor())

internal fun EntityCursor<AbstractDataField>.organizationMapper(): EntityMapper<Organization> =
    OrganizationMapper(organizationCursor())

internal fun EntityCursor<AbstractDataField>.phoneMapper(): EntityMapper<Phone> =
    PhoneMapper(phoneCursor())

internal fun EntityCursor<AbstractDataField>.photoMapper(): EntityMapper<Photo> =
    PhotoMapper(photoCursor())

internal fun EntityCursor<AbstractDataField>.relationMapper(): EntityMapper<Relation> =
    RelationMapper(relationCursor())

internal fun EntityCursor<AbstractDataField>.sipAddressMapper(): EntityMapper<SipAddress> =
    SipAddressMapper(sipAddressCursor())

internal fun EntityCursor<AbstractDataField>.websiteMapper(): EntityMapper<Website> =
    WebsiteMapper(websiteCursor())

@Suppress("UNCHECKED_CAST")
internal fun <T : CommonDataEntity> EntityCursor<AbstractDataField>.entityMapperFor(
    mimeType: MimeType
): EntityMapper<T> = when (mimeType) {
    MimeType.ADDRESS -> addressMapper()
    MimeType.EMAIL -> emailMapper()
    MimeType.EVENT -> eventMapper()
    MimeType.GROUP_MEMBERSHIP -> groupMembershipMapper()
    MimeType.IM -> imMapper()
    MimeType.NAME -> nameMapper()
    MimeType.NICKNAME -> nicknameMapper()
    MimeType.NOTE -> noteMapper()
    MimeType.ORGANIZATION -> organizationMapper()
    MimeType.PHONE -> phoneMapper()
    MimeType.PHOTO -> photoMapper()
    MimeType.RELATION -> relationMapper()
    MimeType.SIP_ADDRESS -> sipAddressMapper()
    MimeType.WEBSITE -> websiteMapper()
    MimeType.UNKNOWN -> throw UnsupportedOperationException(
        "No entity mapper for mime type $mimeType"
    )
} as EntityMapper<T>

// endregion


internal fun RawContactIdCursor.tempRawContactMapper(isProfile: Boolean): EntityMapper<TempRawContact> =
    TempRawContactMapper(this, isProfile)

internal fun EntityCursor<RawContactsField>.blankRawContactMapper(isProfile: Boolean): EntityMapper<BlankRawContact> =
    BlankRawContactMapper(rawContactsCursor(), isProfile)

internal fun EntityCursor<RawContactsField>.rawContactsOptionsMapper(): EntityMapper<Options> =
    OptionsMapper(rawContactsOptionsCursor())

internal fun EntityCursor<ContactsField>.contactsMapper(isProfile: Boolean): EntityMapper<Contact> =
    ContactMapper(contactsCursor(), optionsMapper(), isProfile)

internal fun EntityCursor<ContactsField>.optionsMapper(): EntityMapper<Options> =
    OptionsMapper(optionsCursor())

internal fun EntityCursor<GroupsField>.groupMapper(): EntityMapper<Group> =
    GroupMapper(groupsCursor())