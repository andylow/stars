package com.siberhus.stars.action;

import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.util.Collection;

import javax.servlet.http.HttpServletResponse;

import net.sourceforge.stripes.action.StreamingResolution;
import net.sourceforge.stripes.exception.StripesRuntimeException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JSONObjectResolution extends StreamingResolution {

	public static final String CONTENT_TYPE = "application/json";
	
	private Reader reader;
	
	public JSONObjectResolution(InputStream inputStream) {
		super(CONTENT_TYPE, inputStream);
	}

	public JSONObjectResolution(Reader reader) {
		super(CONTENT_TYPE, reader);
	}
	
	public JSONObjectResolution(Object object) {
		super(CONTENT_TYPE);
		Object jsonObject = null;
		try {
			if(object instanceof Collection){
				jsonObject = new JSONArray((Collection<?>)object);
			}else if(object.getClass().isArray()){
				jsonObject = new JSONArray(object);
			}
		} catch (JSONException e) {
			throw new StripesRuntimeException(e.toString());
		}
		if(jsonObject==null){
			jsonObject = new JSONObject(object);
		}
		this.reader = new StringReader(jsonObject.toString());
	}
	
	public JSONObjectResolution(JSONObject jsonObject) {
		super(CONTENT_TYPE, jsonObject.toString());
	}
	
	public JSONObjectResolution(JSONArray jsonArray) {
		super(CONTENT_TYPE, jsonArray.toString());
	}
	
	@Override
	protected void stream(HttpServletResponse response) throws Exception {
		if(this.reader!=null){
			int length = 0;
			char[] buffer = new char[512];
            PrintWriter out = response.getWriter();
            while ( (length = this.reader.read(buffer)) != -1 ) {
                out.write(buffer, 0, length);
            }
            this.reader.close();
		}else{
			super.stream(response);
		}
	}
	
	
}
