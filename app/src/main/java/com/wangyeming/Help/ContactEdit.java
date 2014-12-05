package com.wangyeming.Help;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.OperationApplicationException;
import android.os.RemoteException;
import android.provider.ContactsContract;

import java.util.ArrayList;

/**
 * Created by Wang on 2014/12/5.
 */
public class ContactEdit {
    protected Long contactId;
    protected ContentResolver cr;

    public ContactEdit(Long contactId, ContentResolver cr) {
        this.contactId = contactId;
        this.cr = cr;
    }

    //修改联系人姓名
    public void updateContactName(String name) throws RemoteException, OperationApplicationException {
        String displayName = name;  //姓名
        String givenName = null;  //姓
        String familyName = null;  //名
        /*
        // 检查是否是英文名称
        if (TextUtil.isEnglishName(displayName) == false) {
            givenName = name.substring(index);
            familyName = name.substring(0, index);
        } else {
            givenName = familyName = displayName;
        }
        */
        givenName = displayName.substring(1);
        familyName = displayName.substring(0, 1);
        System.out.println("givenName " + givenName + "familyName " + familyName);
        //ContentProviderOperation修改联系人姓名
        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
        ops.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                .withSelection(
                        ContactsContract.Data.CONTACT_ID
                        + "=?"
                        + "AND "
                        + ContactsContract.Data.MIMETYPE
                        + " = ?",
                        new String[]{contactId + "",
                                ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE})
                .withValue(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, givenName)
                .withValue(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME, familyName)
                .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, displayName)
                .build());
        try {
            cr.applyBatch(ContactsContract.AUTHORITY, ops);
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (OperationApplicationException e) {
            e.printStackTrace();
        }
        /*
        //传统的方式修改联系人姓名
        values.put(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, givenName);
        values.put(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME, familyName);
        values.put(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, displayName);
        int count = getContentResolver()
                .update(ContactsContract.Data.CONTENT_URI,
                        values,
                        ContactsContract.Data.CONTACT_ID + "=?" + "AND "
                                + ContactsContract.Data.MIMETYPE + " = ?",
                        new String[] { contactId + "",
                                ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE });
                                */
    }

    //修改联系人手机号
    public void updateContactPhoneNum(Long contactId, String number) {
        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
        ops.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                .withSelection(ContactsContract.Data.CONTACT_ID
                                + "=?"
                                + " AND "
                                + ContactsContract.Data.MIMETYPE
                                + "=?"
                                + " AND "
                                + ContactsContract.CommonDataKinds.Organization.TYPE
                                + "=?",
                        new String[] {
                                contactId + "",
                                ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE,
                                String.valueOf(ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE) })
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, number)
                .build());
        try {
            cr.applyBatch(ContactsContract.AUTHORITY, ops);
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (OperationApplicationException e) {
            e.printStackTrace();
        }
    }

    //增加联系人手机号
    public void addContactPhoneNum(Long contactId, String number) {
        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withSelection(ContactsContract.Data.CONTACT_ID
                                + "=?"
                                + " AND "
                                + ContactsContract.Data.MIMETYPE
                                + "=?"
                                + " AND "
                                + ContactsContract.CommonDataKinds.Organization.TYPE
                                + "=?",
                        new String[] {
                                contactId + "",
                                ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE,
                                String.valueOf(ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE) })
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, number)
                .build());
        try {
            cr.applyBatch(ContactsContract.AUTHORITY, ops);
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (OperationApplicationException e) {
            e.printStackTrace();
        }
    }
}
