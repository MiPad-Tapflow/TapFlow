package cn.ljlVink.Tapflow.util

import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import cn.ljlVink.Tapflow.FileSystemInfo
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.topjohnwu.superuser.Shell
import es.dmoral.toasty.Toasty

class utils {
    fun KillApplication(app:String){
        Shell.cmd("am force-stop "+app).exec()
    }
    fun GetDebugInfo():String{
        val lowerdir=Shell.cmd("getprop sys.tapflow.usr.lowerdir").exec().getOut()[0]
        val workdir=Shell.cmd("getprop sys.tapflow.usr.workdir").exec().getOut()[0]
        val upperdir=Shell.cmd("getprop sys.tapflow.usr.upperdir").exec().getOut()[0]
        val result = "lowerdir="+lowerdir+";upperdir="+upperdir+";workdir="+workdir
        return result
    }
    fun grantpermission(ctx:Context){
        if(!XXPermissions.isGranted(ctx, Permission.MANAGE_EXTERNAL_STORAGE)){
            XXPermissions.with(ctx)
                .permission(Permission.MANAGE_EXTERNAL_STORAGE)
                .request(object : OnPermissionCallback {
                    override fun onGranted(permissions: MutableList<String>, allGranted: Boolean) {
                        Toasty.success(ctx,"success.").show()
                    }

                    override fun onDenied(permissions: MutableList<String>, doNotAskAgain: Boolean) {
                        Toasty.success(ctx,"Failed to get file manage").show()
                    }
                })
        }
    }
    fun GetLogs_losetup():String{
        val result = Shell.cmd("cat /dev/Tapflow/losetup_logs").exec()
        val code =result.getCode()
        return if(code==0){
            var ans=""
            for (str in result.out){
                ans+=str+"\n"
            }
            ans
        }else{
            "无日志"
        }
    }

    fun GetModuleVersion():String{
        val result = Shell.cmd("cat /dev/Tapflow/version").exec()
        val code =result.getCode()
        return if(code==0){
            result.out[0]
        }else{
            "Not installed"
        }
    }
    fun getState(): Int {
        val result = Shell.cmd("cat /dev/Tapflow/current").exec()
        val code = result.getCode()
        if (code == 0) {
            val outstr = result.getOut()[0]
            when (outstr) {
                "0" -> return 0
                "1" -> return 1
                "2" -> return 2
            }
        }
        return 3
    }

    fun getEnforcing():String{
        val result = Shell.cmd("getenforce").exec()
        val code = result.getCode()
        if (code == 0) {
            val outstr = result.getOut()[0]
            return if (outstr!=""){
                outstr
            }else{
                "ERROR"
            }
        }
        return "ERROR"

    }
    fun getprop(str: String): String {
        val result = Shell.cmd("getprop "+str).exec()
        val code = result.getCode()
        if (code == 0) {
            val outstr = result.getOut()[0]
            return if (outstr!=""){
                outstr
            }else{
                "ERROR"
            }
        }
        return "ERROR"

    }

    fun getAppVersionInfoWithSpace(context: Context): String {
        var versionName = ""
        var versionCode = ""
        try {
            val pInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            versionName = pInfo.versionName
            versionCode = pInfo.versionCode.toString()
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return "$versionName ($versionCode)"
    }


}