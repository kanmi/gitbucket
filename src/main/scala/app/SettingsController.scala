package app

import service._
import jp.sf.amateras.scalatra.forms._

class SettingsController extends SettingsControllerBase with RepositoryService with AccountService


trait SettingsControllerBase extends ControllerBase { self: RepositoryService with AccountService =>

  case class CollaboratorForm(userName: String)

  val form = mapping(
    "userName" -> trim(label("Username", text(required, existUser)))
  )(CollaboratorForm.apply)

  get("/:owner/:repository/settings") {
    val owner      = params("owner")
    val repository = params("repository")
    redirect("/%s/%s/settings/options".format(owner, repository))
  }
  
  get("/:owner/:repository/settings/options") {
    val owner      = params("owner")
    val repository = params("repository")
    
    settings.html.options(getRepository(owner, repository, servletContext).get)
  }
  
  get("/:owner/:repository/settings/collaborators") {
    val owner      = params("owner")
    val repository = params("repository")
    
    settings.html.collaborators(getCollaborators(owner, repository), getRepository(owner, repository, servletContext).get)
  }

  post("/:owner/:repository/settings/collaborators/_add", form) { form =>
    val owner      = params("owner")
    val repository = params("repository")
    addCollaborator(owner, repository, form.userName)
    redirect("/%s/%s/settings/collaborators".format(owner, repository))
  }

  def existUser: Constraint = new Constraint(){
    def validate(name: String, value: String): Option[String] = {
      getAccountByUserName(value) match {
        case None => Some("User does not exist.")
        case Some(x) if(x.userName == context.loginAccount.get.userName) => Some("User can access this repository already.")
        case Some(x) => None
      }
    }
  }

}