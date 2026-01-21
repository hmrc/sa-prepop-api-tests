import sbt.*

object AppDependencies {

  private lazy val test: Seq[ModuleID] = Seq(
    "uk.gov.hmrc" %% "api-test-runner" % "0.10.0"
  ).map(_ % Test)

  def apply(): Seq[sbt.ModuleID] = test

}
