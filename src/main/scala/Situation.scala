package chess

case class Situation(board: Board, color: Color) {

  lazy val actors = board actorsOf color

  lazy val moves: Map[Pos, List[Move]] = actors collect {
    case actor if actor.moves.nonEmpty ⇒ actor.pos -> actor.moves
  } toMap

  lazy val destinations: Map[Pos, List[Pos]] = moves mapValues { ms ⇒ ms map (_.dest) }

  lazy val kingPos: Option[Pos] = board kingPosOf color

  lazy val check: Boolean = board check color

  def checkMate: Boolean = check && moves.isEmpty

  def staleMate: Boolean = !check && moves.isEmpty

  def autoDraw: Boolean = board.autoDraw

  def threefoldRepetition: Boolean = board.history.threefoldRepetition

  def end: Boolean = checkMate || staleMate || autoDraw

  def move(from: Pos, to: Pos, promotion: Option[PromotableRole]): Valid[Move] = for {
    actor ← board.actors get from toValid "No piece on " + from
    myActor ← actor.validIf(actor is color, "Not my piece on " + from)
    m1 ← myActor.moves find (_.dest == to) toValid "Piece on " + from + " cannot move to " + to
    m2 ← m1 withPromotion promotion toValid "Piece on " + from + " cannot promote to " + promotion
  } yield m2
}

object Situation {

  def apply(variant: Variant): Situation = Situation(Board init variant, White)
}
