<div id = "timeLapseMenu" class = "yuimenubar yuimenubarnav">
	<div class = "bd">
		<ul class = "first-of-type">
			<li class = "yuimenubaritem first-of-type">
				<a class = "yuimenubaritemlabel" id = "homeMenu" href = "${createLink(action: 'index', controller: 'home')}" title="OMAR™ Home">&nbsp;&nbsp;&nbsp;&nbsp;OMAR™ Home</a>
			</li>
			<li class = "yuimenubaritem first-of-type">
				<a class = "yuimenubaritemlabel" href = "#exportMenu">Export</a>
				<div id = "exportMenu" class = "yuimenu">
					<div class = "bd">
						<ul>
							<li class = "yuimenuitem">
								<a class = "yuimenuitemlabel" href = "javascript:exportLink()" title = "Export Link">Link</a>
							</li>
						</ul>
						<ul>
							<li class = "yuimenuitem">
								<a class = "yuimenuitemlabel" href = "javascript:exportImage()" title = "Export Image (Ortho)">Image (Ortho)</a>
							</li>
							<li class = "yuimenuitem">
								<a class = "yuimenuitemlabel" href = "javascript:getUpIsUpImageChipUrl()" title = "Export Image (Up Is Up)">Image (Up Is Up)</a>
							</li>
						</ul>
						<ul>
							<li class = "yuimenuitem">
								<a class = "yuimenuitemlabel" href = "javascript:exportTimeLapseGif()" title = "Export As GIF">GIF</a>
							</li>
							<li class = "yuimenuitem">
								<a class = "yuimenuitemlabel" href = "javascript:exportTimeLapsePdf()" title = "Export As PDF">PDF</a>
							</li>
						</ul>
					</div>
				</div>
			</li>
			<li class = "yuimenubaritem first-of-type">
				<a class = "yuimenubaritemlabel" href = "#toolsMenu">Tools</a>
				<div id = "toolsMenu" class = "yuimenu">
					<div class = "bd">
						<ul>
							<li class = "yuimenuitem">
								<a class = "yuimenuitemlabel" href = "javascript:deleteImageFromTimeLapse()" title = "Delete Image">Delete Image</a>
							</li>
							<li class = "yuimenuitem">
								<a class = "yuimenuitemlabel" href = "javascript:reverseTimeLapseOrder()" title = "Reverse Order">Reverse Order</a>
							</li>
						</ul>
					</div>
				</div>
			</li>
		</ul>
	</div>
</div>

