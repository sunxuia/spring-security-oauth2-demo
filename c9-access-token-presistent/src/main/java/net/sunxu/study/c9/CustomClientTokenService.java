package net.sunxu.study.c9;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.parameters.P;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.ClientTokenServices;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.*;
import java.nio.channels.FileChannel;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 自定义的token 的持久化的实现.
 * 因为只是演示, 所以直接保存到file 中
 */
public class CustomClientTokenService implements ClientTokenServices {
    private ConcurrentHashMap<String, OAuth2AccessToken> repo;

    @PostConstruct
    public void initial() throws IOException {
        File file = new File("d:\\temp.txt");
        StringBuilder sb = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String s;
            while ((s = br.readLine()) != null) {//一次读一行
                sb.append(System.lineSeparator() + s);
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (sb.length() > 0) {
            repo = new ObjectMapper().readValue(sb.toString(), ConcurrentHashMap.class);
        } else {
            repo = new ConcurrentHashMap<>();
        }
    }

    private void save() {
        try {
            String str = new ObjectMapper().writeValueAsString(repo);
            File file = new File("d:\\temp.txt");
            if (file.exists()) {
                file.delete();
            }
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(str);
            fileWriter.close();
        } catch (IOException err) {
            throw new RuntimeException(err);
        }
    }

    @Override
    public OAuth2AccessToken getAccessToken(OAuth2ProtectedResourceDetails resource, Authentication authentication) {
        return repo.get(authentication.getName());
    }

    @Override
    public void saveAccessToken(OAuth2ProtectedResourceDetails resource, Authentication authentication,
                                OAuth2AccessToken accessToken) {
        repo.put(authentication.getName(), accessToken);
        save();
    }

    @Override
    public void removeAccessToken(OAuth2ProtectedResourceDetails resource, Authentication authentication) {
        repo.remove(resource.getTokenName());
    }
}
