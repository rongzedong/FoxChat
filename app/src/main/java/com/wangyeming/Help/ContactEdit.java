package com.wangyeming.Help;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.OperationApplicationException;
import android.os.RemoteException;
import android.provider.ContactsContract;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 联系人编辑
 * 包括：新增联系人、修改联系人信息（姓名，手机号，手机号类型），增加信息（手机号）
 *
 * @author 王小明
 * @data 2015/01/08
 */

public class ContactEdit {
    protected int contactId;
    protected int rawContactId;
    protected ContentResolver cr;

    public ContactEdit(int contactId, int rawContactId, ContentResolver cr) {
        this.contactId = contactId;
        this.rawContactId = rawContactId;
        this.cr = cr;
    }

    public ContactEdit(ContentResolver cr) {
        this.cr = cr;
    }

    //新增联系人
    public void addNewContact(Map<String, Object> newContact) throws RemoteException, OperationApplicationException {
        //1. 提取联系人信息
        String accountType = (String) newContact.get("accountType");  //账户类型
        String accountName = (String) newContact.get("accountName");  //账户名称
        String displayName = (String) newContact.get("displayName");  //显示名
        List <Map <String, Object>> phoneList =
                (List <Map <String, Object>>) newContact.get("phoneList");  //电话列表
        //2.执行新建操作
        ArrayList<ContentProviderOperation> ops = new ArrayList<>();
        rawContactId = 0;
        ops.add(ContentProviderOperation
                .newInsert(ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                .build());  //获取或创建账户
        ops.add(ContentProviderOperation
                .newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(
                        ContactsContract.Data.MIMETYPE,
                        ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, displayName)
                .build());  //增加displayName
        //新增手机号
        for(Map<String, Object> phone: phoneList) {
            String number = (String) phone.get("phone_num");
            int numberTypeId = (int) phone.get("phone_type_id");
            String label = (String) phone.get("phone_label");
            ops.add(ContentProviderOperation.
                    newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactId)
                    .withValue(
                            ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, number)
                    .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
                            numberTypeId)
                    .withValue(ContactsContract.CommonDataKinds.Phone.LABEL, label)
                    .build());
        }
        //TODO 其他信息
        cr.applyBatch(ContactsContract.AUTHORITY, ops);
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
    }

    //修改联系人手机号
    public void updateContactPhoneNum(String number, int numberTypeId) {
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
                                String.valueOf(numberTypeId) })
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

    //修改联系人手机号类型
    public void updateContactPhoneType(String number, int numberTypeId) {
        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
        ops.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                .withSelection(ContactsContract.Data.CONTACT_ID
                                + "=?"
                                + " AND "
                                + ContactsContract.Data.MIMETYPE
                                + "=?"
                                + " AND "
                                + ContactsContract.CommonDataKinds.Phone.NUMBER
                                + "=?",
                        new String[] {
                                contactId + "",
                                ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE,
                                String.valueOf(number) })
                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, numberTypeId)
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
    public void addContactPhoneNum( String number, int numberTypeId, String label) {
        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValue(ContactsContract.Data.RAW_CONTACT_ID, rawContactId)
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, number)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
                        numberTypeId)
                .withValue(ContactsContract.CommonDataKinds.Phone.LABEL, label)
                .build());
        try {
            cr.applyBatch(ContactsContract.AUTHORITY, ops);
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (OperationApplicationException e) {
            e.printStackTrace();
        }
    }

    //删除联系人手机号
    public void deleteContactPhoneNum(String number) {
        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
        ops.add(ContentProviderOperation.newDelete(ContactsContract.Data.CONTENT_URI)
                .withSelection(ContactsContract.Data.CONTACT_ID
                                + "=?"
                                + " AND "
                                + ContactsContract.Data.MIMETYPE
                                + "=?"
                                + " AND "
                                + ContactsContract.CommonDataKinds.Phone.NUMBER
                                + "=?",
                        new String[]{
                                contactId + "",
                                ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE,
                                String.valueOf(number)})
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
