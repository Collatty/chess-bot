package com.collatty.chess.engine.pieces;

import com.collatty.chess.engine.Alliance;
import com.collatty.chess.engine.board.Board;
import com.collatty.chess.engine.board.BoardUtils;
import com.collatty.chess.engine.board.Move;
import com.collatty.chess.engine.board.Tile;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class King extends Piece{

    private final static int[] CANDIDATE_MOVE_COORDINATE = { -9, -8, -7, -1, 1, 7, 8, 9 };



    private final boolean kingSideCastleCapable;
    private final boolean queenSideCastleCapable;
    private final boolean isCastled;

    public boolean isCastled() {
        return isCastled;
    }

    public King(final Alliance pieceAlliance, final int piecePosition, final boolean kingSideCastledCapable,
                final boolean queenSideCastleCapable) {
        super(PieceType.KING, piecePosition, pieceAlliance, true);
        this.kingSideCastleCapable = kingSideCastledCapable;
        this.queenSideCastleCapable = queenSideCastleCapable;
        this.isCastled = false;
    }

    public King(final Alliance pieceAlliance, final int piecePosition, final boolean isFirstMove,
                final boolean isCasteld, final boolean kingSideCastleCapable, final boolean queenSideCastleCapable) {
        super(PieceType.KING, piecePosition, pieceAlliance, isFirstMove);
        this.isCastled = isCasteld;
        this.kingSideCastleCapable = kingSideCastleCapable;
        this.queenSideCastleCapable = queenSideCastleCapable;
    }

    public boolean isKingSideCastleCapable() {
        return kingSideCastleCapable;
    }

    public boolean isQueenSideCastleCapable() {
        return queenSideCastleCapable;
    }


    @Override
    public Collection<Move> calculateLegalMoves(final Board board) {

        final List<Move> legalMoves = new ArrayList<Move>();


        for (final int currentCandidateOffset : CANDIDATE_MOVE_COORDINATE) {
            final int candidateDestinationCoordinate = this.piecePosition + currentCandidateOffset;

            if (isFirstColumnExclusion(this.piecePosition, currentCandidateOffset) ||
                    isEighthColumnExclusion(this.piecePosition, currentCandidateOffset)){
                continue;

            }

            if (BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate)) {
                final Tile candididateDestinationTile = board.getTile(candidateDestinationCoordinate);
                if (!candididateDestinationTile.isTileOccupied()) {
                    legalMoves.add(new Move.MajorMove(board, this, candidateDestinationCoordinate));
                } else {
                    final Piece pieceAtDestination = candididateDestinationTile.getPiece();
                    final Alliance pieceAlliance = pieceAtDestination.getPieceAlliance();

                    if (this.pieceAlliance != pieceAlliance) {
                        legalMoves.add(new Move.MajorAttackMove(board, this, candidateDestinationCoordinate,
                                pieceAtDestination));
                    }
                }
            }

        }
        return ImmutableList.copyOf(legalMoves);
    }

    @Override
    public King movePiece(Move move) {
        return new King(move.getMovedPiece().getPieceAlliance(), move.getDestinationCoordinate(), false,
                move.isCastlingMove(), false, false);
    }

    @Override
    public String toString() {
        return PieceType.KING.toString();
    }

    private static boolean isFirstColumnExclusion(final int currentPosition, final int candidateOffset) {
        return BoardUtils.FIRST_COLUMN[currentPosition] && (candidateOffset == -1 || candidateOffset == -9 || candidateOffset == 7);
    }

    private static boolean isEighthColumnExclusion(final int currentPosition, final int candidateOffset) {
        return BoardUtils.EIGHTH_COLUMN[currentPosition] && (candidateOffset == 1 || candidateOffset == -7 || candidateOffset == 9);
    }
}
