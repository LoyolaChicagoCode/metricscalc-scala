import scala.collection.mutable.{Set => MSet}
import jp.ne.opt.chronoscala.Imports._
import io.circe.generic.auto.exportDecoder
import io.circe.parser.decode
import com.google.common.collect.{Range, TreeRangeMap}

import java.time.Duration

/**
  * Represents GitHub issues exported as follows:
  * `gh issue list -s all --json number,createdAt,closedAt`
  */
case class Issue(number: Int, createdAt: Instant, closedAt: Option[Instant])

sealed trait Event {
  def issue: Issue
  def instant: Instant
}
case class Create(issue: Issue) extends Event {
  override def instant: Instant = issue.createdAt
}
case class Close(issue: Issue) extends Event {
  override def instant: Instant = issue.closedAt.get
}

object IssueSpoilage extends App {

  // read issues as single string
  val input = scala.io.Source.stdin.getLines().mkString
  println(input)

  // parse and convert to typed data structure
  val issues = decode[List[Issue]](input).toOption.get
  println(issues)

  val events = issues.flatMap {
    case i @ Issue(n, c, None)    => List(Create(i))
    case i @ Issue(n, c, Some(x)) => List(Create(i), Close(i))
  }.sortBy(_.instant)

  for (p <- events.sliding(2)) {
    assert(p(0).instant <= p(1).instant)
  }
  println(events)

  // create range map for efficient queries based on date/time instants
  // each range maps to the issues open during that range
  val now = Instant.now()
  val issueMap = TreeRangeMap.create[Instant, Set[Issue]]()
  val issueSet = MSet.empty[Issue]
  for (e <- events) {
    val r = Range.closedOpen(e.instant, now)
    e match {
      case Create(i) =>
        issueSet += i
      case Close(i) =>
        issueSet -= i
    }
    issueMap.put(r, issueSet.toSet)
    println(s"processing event $e -> ${issueSet.size} issue(s)")
  }

  println(issueMap)
  println(issueMap.asMapOfRanges().keySet().size)

  def issueSpoilageAt(instant: Instant): Map[Issue, Duration] = {
    val pairs = issueMap.get(instant).map {
      case i @ Issue(_, createdAt, _) => i -> Duration.between(createdAt, instant)
    }
    pairs.toMap
  }

  // TODO could calculate issue spoilage as weighted average based on issue severity

  println(issueSpoilageAt(Instant.parse("2020-12-15T00:00:00Z")))
}
