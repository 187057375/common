package org.wsh.common.test.eum;


import static org.wsh.common.test.eum.WithdrawStatus.AUDIT_SUCCESS;

/**
 * @author wsh
 * @JDK-version: JDK1.8
 * @comments: 对象OBJ转枚举
 * @since Date： 2016-08-25 15:48
 */
public class Test {

    @org.junit.Test
    public void test(){
        Object obj = "AUDIT_SUCCESS";
        System.out.println(obj);
        WithdrawStatus withdrawStatus = EnumUtil.objToEnum(obj);
        System.out.println(withdrawStatus);
        System.out.println(withdrawStatus.equals(AUDIT_SUCCESS));
        System.out.println(withdrawStatus == AUDIT_SUCCESS);
        System.out.println();

        System.out.println(EnumUtil.EnumToString(WithdrawStatus.BADGATEWAY));

        Person person1 = Person.MAN;
        System.out.println(person1);
        Person.MAN.work();
        Person.WOMEN.work();
    }
}
