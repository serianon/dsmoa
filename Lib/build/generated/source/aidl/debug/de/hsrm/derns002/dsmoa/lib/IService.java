/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: /home/serian/GitHub/dsmoa/Lib/src/main/aidl/de/hsrm/derns002/dsmoa/lib/IService.aidl
 */
package de.hsrm.derns002.dsmoa.lib;
public interface IService extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements de.hsrm.derns002.dsmoa.lib.IService
{
private static final java.lang.String DESCRIPTOR = "de.hsrm.derns002.dsmoa.lib.IService";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an de.hsrm.derns002.dsmoa.lib.IService interface,
 * generating a proxy if needed.
 */
public static de.hsrm.derns002.dsmoa.lib.IService asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof de.hsrm.derns002.dsmoa.lib.IService))) {
return ((de.hsrm.derns002.dsmoa.lib.IService)iin);
}
return new de.hsrm.derns002.dsmoa.lib.IService.Stub.Proxy(obj);
}
@Override public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_registerListener:
{
data.enforceInterface(DESCRIPTOR);
de.hsrm.derns002.dsmoa.lib.IServiceListener _arg0;
_arg0 = de.hsrm.derns002.dsmoa.lib.IServiceListener.Stub.asInterface(data.readStrongBinder());
this.registerListener(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_unregisterListener:
{
data.enforceInterface(DESCRIPTOR);
de.hsrm.derns002.dsmoa.lib.IServiceListener _arg0;
_arg0 = de.hsrm.derns002.dsmoa.lib.IServiceListener.Stub.asInterface(data.readStrongBinder());
boolean _result = this.unregisterListener(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_setLabelForCurrentCluster:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
boolean _result = this.setLabelForCurrentCluster(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_removeLabelForCurrentCluster:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.removeLabelForCurrentCluster();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_removeAllLabels:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.removeAllLabels();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements de.hsrm.derns002.dsmoa.lib.IService
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
@Override public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
// Listener

@Override public void registerListener(de.hsrm.derns002.dsmoa.lib.IServiceListener listener) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((listener!=null))?(listener.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_registerListener, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public boolean unregisterListener(de.hsrm.derns002.dsmoa.lib.IServiceListener listener) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((listener!=null))?(listener.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_unregisterListener, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
// Labels

@Override public boolean setLabelForCurrentCluster(java.lang.String labelName) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(labelName);
mRemote.transact(Stub.TRANSACTION_setLabelForCurrentCluster, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public boolean removeLabelForCurrentCluster() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_removeLabelForCurrentCluster, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public boolean removeAllLabels() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_removeAllLabels, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
}
static final int TRANSACTION_registerListener = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_unregisterListener = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_setLabelForCurrentCluster = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_removeLabelForCurrentCluster = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
static final int TRANSACTION_removeAllLabels = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
}
// Listener

public void registerListener(de.hsrm.derns002.dsmoa.lib.IServiceListener listener) throws android.os.RemoteException;
public boolean unregisterListener(de.hsrm.derns002.dsmoa.lib.IServiceListener listener) throws android.os.RemoteException;
// Labels

public boolean setLabelForCurrentCluster(java.lang.String labelName) throws android.os.RemoteException;
public boolean removeLabelForCurrentCluster() throws android.os.RemoteException;
public boolean removeAllLabels() throws android.os.RemoteException;
}
