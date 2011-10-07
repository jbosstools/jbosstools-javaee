package org.jboss.test605;

/**
 * 6.5. Configuring methods
 * It is also possible to configure methods in a similar way to configuring fields:
<test605:MethodBean>
    <test605:doStuff>
        <s:Produces/>
    </test605:doStuff>      

    <test605:doStuff>
        <s:Produces/>
        <test605:Qualifier1/>
        <s:parameters>
            <s:Long>
                <test605:Qualifier2/>
            </s:Long>
        </s:parameters>
    </test605:doStuff>

    <test605:doStuff>
        <s:Produces/>
        <test605:Qualifier1/>
        <s:parameters>
            <s:array dimensions="2">
                <test605:Qualifier2/>
                <s:Long/>
            </s:array>
        </s:parameters>
    </test605:doStuff>
</test605:MethodBean>
 *
 */
public class MethodBean {

	public int doStuff() {
		return 1;
	}

	public long doStuff(long c) {
		return c + 1;
	}

	public void doStuff(long[][] beans) {

	}
}
