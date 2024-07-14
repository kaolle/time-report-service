package pb.se.TimeReportService.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class JwtResponse {
	@JsonProperty
	private final String token;
	@JsonProperty
	private final String id;
	@JsonProperty
	private final String username;
	@JsonProperty
	private final List<String> roles;

	public JwtResponse(String accessToken, String id, String username, List<String> roles) {
		this.token = accessToken;
		this.id = id;
		this.username = username;
		this.roles = roles;
	}

	public String getAccessToken() {
		return token;
	}

	public String getTokenType() {
		return "Bearer";
	}

	public String getId() {
		return id;
	}

	public String getUsername() {
		return username;
	}

	public List<String> getRoles() {
		return roles;
	}
}
