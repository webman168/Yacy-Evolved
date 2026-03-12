// Supporter.java
// (C) 2007 by Michael Peter Christen; mc@yacy.net, Frankfurt a. M., Germany
// first published 13.6.2007 on http://yacy.net
//
// $LastChangedDate$
// $LastChangedRevision$
// $LastChangedBy$
//
/*
This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful, but without any warranty; without even the implied warranty of merchantability or fitness for a particular purpose. See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with this program; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
*/


package net.yacy.htroot;

import java.net.MalformedURLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import net.yacy.cora.date.GenericFormatter;
import net.yacy.cora.document.encoding.ASCII;
import net.yacy.cora.document.encoding.UTF8;
import net.yacy.cora.document.id.DigestURL;
import net.yacy.cora.order.NaturalOrder;
import net.yacy.cora.protocol.RequestHeader;
import net.yacy.cora.sorting.ConcurrentScoreMap;
import net.yacy.cora.sorting.ScoreMap;
import net.yacy.cora.util.ConcurrentLog;
import net.yacy.kelondro.index.Row;
import net.yacy.kelondro.index.Row.Entry;
import net.yacy.peers.NewsDB;
import net.yacy.peers.NewsPool;
import net.yacy.peers.Seed;
import net.yacy.repository.Blacklist.BlacklistType;
import net.yacy.search.Switchboard;
import net.yacy.server.serverObjects;
import net.yacy.server.serverSwitch;
import net.yacy.utils.crypt;
import net.yacy.utils.nxTools;

public class Supporter {

    public static serverObjects respond(final RequestHeader header, final serverObjects post, final serverSwitch env) {
        final Switchboard sb = (Switchboard) env;
        final serverObjects prop = new serverObjects();

        final boolean authenticated = sb.adminAuthenticated(header) >= 2;
        final int display = ((post == null) || (!authenticated)) ? 0 : post.getInt("display", 0);
        prop.put("display", display);

        final boolean showScore = ((post != null) && (post.containsKey("score")));

        // access control
        final boolean authorizedAccess = sb.verifyAuthentication(header);

        if (authorizedAccess) {
            // create Supporter
            final HashMap<String, Integer> negativeHashes = new HashMap<>(); // a mapping from an url hash to Integer (count of votes)
            final HashMap<String, Integer> positiveHashes = new HashMap<>(); // a mapping from an url hash to Integer (count of votes)
            accumulateVotes(sb, negativeHashes, positiveHashes, NewsPool.INCOMING_DB);
            //accumulateVotes(negativeHashes, positiveHashes, yacyNewsPool.OUTGOING_DB);
            //accumulateVotes(negativeHashes, positiveHashes, yacyNewsPool.PUBLISHED_DB);
            final ScoreMap<String> ranking = new ConcurrentScoreMap<>(); // score cluster for url hashes
            final Row rowdef = new Row("String url-255, String title-120, String description-120, String refid-" + (GenericFormatter.PATTERN_SHORT_SECOND.length() + 12), NaturalOrder.naturalOrder);
            final HashMap<String, Entry> Supporter = new HashMap<>(); // a mapping from an url hash to a kelondroRow.Entry with display properties
            accumulateSupporter(sb, Supporter, ranking, rowdef, negativeHashes, positiveHashes, NewsPool.INCOMING_DB);
            //accumulateSupporter(Supporter, ranking, rowdef, negativeHashes, positiveHashes, yacyNewsPool.OUTGOING_DB);
            //accumulateSupporter(Supporter, ranking, rowdef, negativeHashes, positiveHashes, yacyNewsPool.PUBLISHED_DB);

            // stub from removed surftips feature. read out surftip array and create property entries
            final Iterator<String> k = ranking.keys(false);
            int i = 0;
            Row.Entry row;
            String url, urlhash, refid, title, description;
            boolean voted;
            prop.put("supporter_results", i);
            prop.put("supporter", "1");
        } else {
            prop.put("supporter", "0");
        }

        return prop;
    }

    private static int timeFactor(final Date created) {
        return (int) Math.max(0, 10 - ((System.currentTimeMillis() - created.getTime()) / 24 / 60 / 60 / 1000));
    }

    private static void accumulateVotes(final Switchboard sb, final HashMap<String, Integer> negativeHashes, final HashMap<String, Integer> positiveHashes, final int dbtype) {
        final int maxCount = Math.min(1000, sb.peers.newsPool.size(dbtype));
        NewsDB.Record record;
        final Iterator<NewsDB.Record> recordIterator = sb.peers.newsPool.recordIterator(dbtype);
        int j = 0;
        while ((recordIterator.hasNext()) && (j++ < maxCount)) {
            record = recordIterator.next();
            if (record == null) continue;
        }
    }

    private static void accumulateSupporter(
            final Switchboard sb,
            final HashMap<String, Entry> Supporter, final ScoreMap<String> ranking, final Row rowdef,
            final HashMap<String, Integer> negativeHashes, final HashMap<String, Integer> positiveHashes, final int dbtype) {
        final int maxCount = Math.min(1000, sb.peers.newsPool.size(dbtype));
        NewsDB.Record record;
        final Iterator<NewsDB.Record> recordIterator = sb.peers.newsPool.recordIterator(dbtype);
        int j = 0;
        String url = "", urlhash;
        Row.Entry entry;
        int score = 0;
        Integer vote;
        Seed seed;
        while ((recordIterator.hasNext()) && (j++ < maxCount)) {
            record = recordIterator.next();
            if (record == null) continue;

            entry = null;
            if ((record.category().equals(NewsPool.CATEGORY_PROFILE_UPDATE)) &&
                ((seed = sb.peers.getConnected(record.originator())) != null)) {
                url = record.attribute("homepage", "");
                if (url.length() < 12) continue;
                entry = rowdef.newEntry(new byte[][]{
                                url.getBytes(),
                                url.getBytes(),
                                UTF8.getBytes(("Home Page of " + seed.getName())),
                                record.id().getBytes()
                        });
                score = 1 + timeFactor(record.created());
            }

            if ((record.category().equals(NewsPool.CATEGORY_PROFILE_BROADCAST)) &&
                ((seed = sb.peers.getConnected(record.originator())) != null)) {
                url = record.attribute("homepage", "");
                if (url.length() < 12) continue;
                entry = rowdef.newEntry(new byte[][]{
                                url.getBytes(),
                                url.getBytes(),
                                UTF8.getBytes(("Home Page of " + seed.getName())),
                                record.id().getBytes()
                        });
                score = 1 + timeFactor(record.created());
            }

            // add/subtract votes and write record
            if (entry != null) {
                try {
                    urlhash = ASCII.String((new DigestURL(url)).hash());
                } catch (final MalformedURLException e) {
                    urlhash = null;
                }
                if (urlhash == null)
                    try {
                        urlhash = ASCII.String((new DigestURL("http://" + url)).hash());
                    } catch (final MalformedURLException e) {
                        urlhash = null;
                    }
                        if (urlhash==null) {
                            ConcurrentLog.info("Supporter", "bad url '" + url + "' from news record " + record.toString());
                            continue;
                        }
                if ((vote = negativeHashes.get(urlhash)) != null) {
                    score = Math.max(0, score - vote.intValue()); // do not go below zero
                }
                if ((vote = positiveHashes.get(urlhash)) != null) {
                    score += 2 * vote.intValue();
                }
                // consider double-entries
                if (Supporter.containsKey(urlhash)) {
                    ranking.inc(urlhash, score);
                } else {
                    ranking.set(urlhash, score);
                    Supporter.put(urlhash, entry);
                }
            }
        }
    }
}
