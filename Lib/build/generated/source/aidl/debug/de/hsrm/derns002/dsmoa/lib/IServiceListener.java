/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: /home/serian/GitHub/dsmoa/Lib/src/main/aidl/de/hsrm/derns002/dsmoa/lib/IServiceListener.aidl
 */
package de.hsrm.derns002.dsmoa.lib;
public interface IServiceListener extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements de.hsrm.derns002.dsmoa.lib.IServiceListener
{
private static final java.lang.String DESCRIPTOR = "de.hsrm.derns002.dsmoa.lib.IServiceListener";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an de.hsrm.derns002.dsmoa.lib.IServiceListener interface,
 * generating a proxy if needed.
 */
public static de.hsrm.derns002.dsmoa.lib.IServiceListener asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof de.hsrm.derns002.dsmoa.lib.IServiceListener))) {
return ((de.hsrm.derns002.dsmoa.lib.IServiceListener)iin);
}
return new de.hsrm.derns002.dsmoa.lib.IServiceListener.Stub.Proxy(obj);
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
case TRANSACTION__onLabeledClusterEnter:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
this._onLabeledClusterEnter(_arg0);
return true;
}
case TRANSACTION__onLabeledClusterExit:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
this._onLabeledClusterExit(_arg0);
return true;
}
case TRANSACTION__onUnlabeledClusterEnter:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this._onUnlabeledClusterEnter(_arg0);
return true;
}
case TRANSACTION__onUnlabeledClusterExit:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this._onUnlabeledClusterExit(_arg0);
return true;
}
case TRANSACTION__onPredictionChange:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
this._onPredictionChange(_arg0);
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements de.hsrm.derns002.dsmoa.lib.IServiceListener
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
// Clustering

@Override public void _onLabeledClusterEnter(java.lang.String clusterName) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(clusterName);
mRemote.transact(Stub.TRANSACTION__onLabeledClusterEnter, _data, null, android.os.IBinder.FLAG_ONEWAY);
}
finally {
_data.recycle();
}
}
@Override public void _onLabeledClusterExit(java.lang.String clusterName) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(clusterName);
mRemote.transact(Stub.TRANSACTION__onLabeledClusterExit, _data, null, android.os.IBinder.FLAG_ONEWAY);
}
finally {
_data.recycle();
}
}
@Override public void _onUnlabeledClusterEnter(int clusterId) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(clusterId);
mRemote.transact(Stub.TRANSACTION__onUnlabeledClusterEnter, _data, null, android.os.IBinder.FLAG_ONEWAY);
}
finally {
_data.recycle();
}
}
@Override public void _onUnlabeledClusterExit(int clusterId) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(clusterId);
mRemote.transact(Stub.TRANSACTION__onUnlabeledClusterExit, _data, null, android.os.IBinder.FLAG_ONEWAY);
}
finally {
_data.recycle();
}
}
// Predictions

@Override public void _onPredictionChange(java.lang.String clusterName) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(clusterName);
mRemote.transact(Stub.TRANSACTION__onPredictionChange, _data, null, android.os.IBinder.FLAG_ONEWAY);
}
finally {
_data.recycle();
}
}
}
static final int TRANSACTION__onLabeledClusterEnter = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION__onLabeledClusterExit = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION__onUnlabeledClusterEnter = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION__onUnlabeledClusterExit = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
static final int TRANSACTION__onPredictionChange = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
}
// Clustering

public void _onLabeledClusterEnter(java.lang.String clusterName) throws android.os.RemoteException;
public void _onLabeledClusterExit(java.lang.String clusterName) throws android.os.RemoteException;
public void _onUnlabeledClusterEnter(int clusterId) throws android.os.RemoteException;
public void _onUnlabeledClusterExit(int clusterId) throws android.os.RemoteException;
// Predictions

public void _onPredictionChange(java.lang.String clusterName) throws android.os.RemoteException;
}
