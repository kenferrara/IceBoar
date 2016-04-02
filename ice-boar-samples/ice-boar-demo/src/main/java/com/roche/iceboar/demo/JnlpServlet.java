/*
 * ****************************************************************************
 *  Copyright © 2015 Hoffmann-La Roche
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 * ****************************************************************************
 */

package com.roche.iceboar.demo;

import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.HashSet;
import java.util.Set;

/**
 * This class handle all HTTP requests *.jnlp files (defined in web.xml).
 */
public class JnlpServlet extends HttpServlet {

    /**
     * This method handle all HTTP requests for *.jnlp files (defined in web.xml). Method check, is name correct
     * (allowed), read file from disk, replace #{codebase} (it's necessary to be generated based on where application
     * is deployed), #{host} () and write to the response.
     * <p>
     * You can use this class in your code for downloading JNLP files.
     * Return a content of requested jnlp file in response.
     *
     * @throws IOException when can't close some stream
     */
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String contextPath = request.getContextPath();
        String requestURI = request.getRequestURI();
        String host = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
        String codebase = host + contextPath;
        String filename = StringUtils.removeStart(requestURI, contextPath);
        response.setContentType("application/x-java-jnlp-file");
        response.addHeader("Pragma", "no-cache");
        response.addHeader("Expires", "-1");

        OutputStreamWriter out = new OutputStreamWriter(response.getOutputStream());

        InputStream in = JnlpServlet.class.getResourceAsStream(filename);
        if (in == null) {
            error(response, "Can't open: " + filename);
            return;
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));

        String line = reader.readLine();
        while (line != null) {
            line = line.replace("#{codebase}", codebase);
            line = line.replace("#{host}", host);
            out.write(line);
            out.write("\n");
            line = reader.readLine();
        }

        out.flush();
        out.close();
        reader.close();
    }

    private void error(HttpServletResponse response, String message) throws IOException {
        System.out.println(message);
        response.sendError(404);
    }


}
