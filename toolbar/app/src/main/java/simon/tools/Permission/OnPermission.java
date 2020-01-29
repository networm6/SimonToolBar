package simon.tools.Permission;
import java.util.List;

public interface OnPermission {

    /**
     * 有权限被同意授予时回调
     *
     * @param granted           请求成功的权限组
     * @param isAll             是否全部授予了
     */
    void hasPermission(List<String> granted, boolean isAll);

    /**
     * 有权限被拒绝授予时回调
     *
     * @param denied            请求失败的权限组
     * @param quick             是否有某个权限被永久拒绝了
     */
    void noPermission(List<String> denied, boolean quick);
}
