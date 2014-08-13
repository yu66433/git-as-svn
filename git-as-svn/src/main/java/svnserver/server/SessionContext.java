package svnserver.server;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import svnserver.StringHelper;
import svnserver.parser.SvnServerParser;
import svnserver.parser.SvnServerWriter;
import svnserver.repository.Repository;
import svnserver.server.error.ClientErrorException;
import svnserver.server.msg.ClientInfo;
import svnserver.server.step.Step;

import java.util.*;

/**
 * SVN client session context.
 *
 * @author Artem V. Navrotskiy <bozaro@users.noreply.github.com>
 */
public class SessionContext {
  @NotNull
  private final SvnServerParser parser;
  @NotNull
  private final SvnServerWriter writer;
  @NotNull
  private final Deque<Step> stepStack = new ArrayDeque<>();
  @NotNull
  private final Repository repository;
  @NotNull
  private final String baseUrl;
  @NotNull
  private final Set<String> capabilities;
  @NotNull
  private String parent;

  public SessionContext(@NotNull SvnServerParser parser, @NotNull SvnServerWriter writer, @NotNull Repository repository, @NotNull String baseUrl, @NotNull ClientInfo clientInfo) {
    this.parser = parser;
    this.writer = writer;
    this.repository = repository;
    this.baseUrl = baseUrl + (baseUrl.endsWith("/") ? "" : "/");
    this.capabilities = new HashSet<>(Arrays.asList(clientInfo.getCapabilities()));
    setParent(clientInfo.getUrl());
  }

  public boolean hasCapability(@NotNull String capability) {
    return capabilities.contains(capability);
  }

  public void setParent(@NotNull String parent) {
    this.parent = baseUrl.equals(parent + '/') ? baseUrl : parent;
  }

  @NotNull
  public String getRepositoryPath(@Nullable String localPath) throws ClientErrorException {
    if (!parent.startsWith(baseUrl)) {
      throw new ClientErrorException(0, "Invalid current path prefix: " + parent + " (base: " + baseUrl + ")");
    }
    return StringHelper.joinPath(parent.substring(baseUrl.length() - 1), localPath);
  }

  @NotNull
  public Repository getRepository() {
    return repository;
  }

  @NotNull
  public SvnServerParser getParser() {
    return parser;
  }

  @NotNull
  public SvnServerWriter getWriter() {
    return writer;
  }

  public void push(@NotNull Step step) {
    stepStack.push(step);
  }

  @Nullable
  public Step poll() {
    return stepStack.poll();
  }

}