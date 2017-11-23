LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := optional
LOCAL_SRC_FILES := $(call all-java-files-under, src) \ /src/com/lenkeng/bean/ImplInter.aidl /src/com/lenkeng/bean/InterfaceStub.aidl /src/lenkeng/com/welcome/server/LKHomeInterface.aidl
LOCAL_PACKAGE_NAME :=LKHome
LOCAL_CERTIFICATE := platform
LOCAL_PROGUARD_ENABLED := disabled
LOCAL_DEX_PREOPT := false





LOCAL_STATIC_JAVA_LIBRARIES := xmpp jackon android-support-v4 httpclient httpcore
include $(BUILD_PACKAGE)

include $(CLEAR_VARS)
LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES := xmpp:libs/asmack.jar jackon:libs/jackson-all-1.9.2.jar httpclient:libs/httpclient-4.2.5.jar httpcore:libs/httpcore-4.3.3.jar
include $(BUILD_MULTI_PREBUILT)    

