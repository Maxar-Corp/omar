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
							<li class = "yuimenuitem">
								<a class = "yuimenuitemlabel" href = 'javascript:$("#exportImageDialog").dialog("open")' title = "Export Image">Image</a>
							</li>
							<li class = "yuimenuitem">
								<a class = "yuimenuitemlabel" href = 'javascript:$("#exportAnimationDialog").dialog("open")' title = "Export Animation">Animation</a>
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

