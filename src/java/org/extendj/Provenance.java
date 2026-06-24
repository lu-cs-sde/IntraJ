/***************************************************************************
 * Copyright (C) 2026 The JastAdd Team
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its
 * contributors may be used to endorse or promote products derived from this
 * software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package org.extendj;

import java.io.InputStream;
import java.util.Properties;

/**
 * IntraJ provenance information
 */
public class Provenance {
    private static final String RESOURCE = "/IntraJBuildInfo.properties";
    private static final Properties PROPS = load();

    private static Properties load() {
        Properties props = new Properties();
        try (InputStream inStream = Provenance.class.getResourceAsStream(RESOURCE)) {
            if (inStream != null) {
                props.load(inStream);
            }
        } catch (Exception e) {
            // Failed to get provenance information
        }
        return props;
    }

    private static String getProp(String key) {
        return PROPS.getProperty(key, "");
    }

    public static final String INTRAJ_COMMIT       = getProp("intraj.commit");
    public static final String INTRAJ_COMMIT_DATE  = getProp("intraj.commit.date");
    public static final String INTRAJ_VARIANT      = getProp("build.variant"); // basic-stacked or relaxed-stacked?
    public static final String EXTENDJ_COMMIT      = getProp("extendj.commit");
    public static final String INTRACFG_COMMIT     = getProp("intracfg.commit");
    public static final String JASTADD2_VERSION    = getProp("jastadd2.version");
    public static final String JASTADD2_JAR        = getProp("jastadd2.jar");
    public static final String JASTADD2_JAR_SHA256 = getProp("jastadd2.jar.sha256");
    public static final String JASTADD2_OPTIONS    = getProp("jastadd2.options");
}
