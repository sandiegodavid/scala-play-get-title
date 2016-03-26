package controllers

import models.PageTitle

import play.api._
import play.api.data._
import play.api.data.Forms._
import play.api.i18n.{ I18nSupport, MessagesApi }
import play.api.mvc._
import play.filters.csrf.CSRFConfig
import scala.util.{ Failure, Success }
import scala.concurrent.duration._

import com.stackmob.newman._
import com.stackmob.newman.dsl._
import scala.concurrent._
import scala.concurrent.duration._
import java.net.URL
import scala.util.matching.Regex

import scala.collection.mutable.ArrayBuffer

class Application(val messagesApi: MessagesApi)(implicit csrfConfig: CSRFConfig) extends Controller with I18nSupport {

  private val pages = ArrayBuffer(
    PageTitle("CNN.com", "CNN - Breaking News, Latest News and Videos"),
    PageTitle("Yahoo.com", "Yahoo"),
    PageTitle("Google.com", "Google"))

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  def listPages = Action { implicit request =>
    // Pass an unpopulated form to the template
    Ok(views.html.listPages(pages.toSeq, Application.createPageForm))
  }

  // This will be the action that handles our form post
  def createPageTitleEntry = Action { implicit request =>
    val formValidationResult = Application.createPageForm.bindFromRequest
    formValidationResult.fold({ formWithErrors =>
      // This is the bad case, where the form had validation errors.
      // Let's show the user the form again, with the errors highlighted.
      // Note how we pass the form with errors to the template.
      BadRequest(views.html.listPages(pages.toSeq, formWithErrors))
    }, { pageUrl =>
      // This is the good case, where the form was successfully parsed as a String.
      implicit val httpClient = new ApacheHttpClient
      //execute a GET request
      val url = new URL("http://" + pageUrl)
      val response = Await.result(GET(url).apply, 1.minute)
      val matchList = ("""(?s)<title>.*</title>""".r findFirstIn response.bodyString).getOrElse("<title>page has no title</title>")
      
      pages.append(PageTitle(pageUrl, matchList.substring(7, matchList.length - 8)))
      Redirect(routes.Application.listPages)
    })
  }
}

object Application {

  /**
   * The form definition for the "Get page title" form.
   *  It specifies the form fields and their types,
   *  as well as how to convert from a Widget to form data and vice versa.
   */
  val createPageForm = Form(
    single(
      "pageUrl" -> text))

}
